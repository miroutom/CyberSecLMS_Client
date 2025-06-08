package api

import (
	"fmt"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"lmsmodule/api-gateway/internal/circuitbreaker"
	"lmsmodule/api-gateway/internal/metrics"
	"lmsmodule/api-gateway/internal/models"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"strings"
	"time"

	"github.com/gin-gonic/gin"

	"lmsmodule/api-gateway/internal/discovery"
	"lmsmodule/api-gateway/internal/middleware"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type Server struct {
	Router          *gin.Engine
	Config          *utils.Config
	Logger          *logger.Logger
	HttpClient      *http.Client
	Discovery       *discovery.ServiceDiscovery
	Metrics         *metrics.ServiceMetrics
	CircuitBreakers map[string]*circuitbreaker.CircuitBreaker
}

func NewServer(config *utils.Config, logger *logger.Logger) *Server {
	dsn := "user:pass@tcp(db:3306)/dbname?charset=utf8mb4&parseTime=True&loc=Local"
	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		panic("failed to connect database")
	}

	if err := db.AutoMigrate(&models.Lab{}); err != nil {
		panic(err)
	}

	labHandler := NewLabHandler(db)
	submissionHandler := NewSubmissionHandler(db)

	router := gin.New()
	router.Use(middleware.LoggerMiddleware(logger))
	router.Use(gin.Recovery())
	router.Use(middleware.SecurityHeadersMiddleware())

	router.GET("/api/labs", labHandler.GetLabs)
	router.GET("/api/labs/:id", labHandler.GetLab)
	router.POST("/api/labs/:id/submit", submissionHandler.SubmitSolution)

	if os.Getenv("INIT_LABS") == "true" {
		if err := labHandler.InitLabs(); err != nil {
			panic(err)
		}
	}

	httpClient := &http.Client{
		Timeout: time.Duration(30) * time.Second,
	}

	serviceDiscovery, err := discovery.NewServiceDiscovery(config, logger)
	if err != nil {
		logger.Error("Failed to initialize service discovery: %v", err)
	}

	server := &Server{
		Router:          router,
		Config:          config,
		Logger:          logger,
		HttpClient:      httpClient,
		Discovery:       serviceDiscovery,
		Metrics:         metrics.NewServiceMetrics(),
		CircuitBreakers: make(map[string]*circuitbreaker.CircuitBreaker),
	}

	server.CircuitBreakers["BACKEND-SERVICE"] = circuitbreaker.NewCircuitBreaker("BACKEND-SERVICE", 5, 30*time.Second)
	server.CircuitBreakers["EXECUTOR-SVC"] = circuitbreaker.NewCircuitBreaker("EXECUTOR-SVC", 5, 30*time.Second)

	server.setupRoutes()
	return server
}

func (s *Server) setupRoutes() {
	SetupRoutes(s.Router, s.ProxyRequest)
	s.setupScalingRoutes()
}

func (s *Server) ProxyRequest(targetServiceName string) gin.HandlerFunc {
	return func(c *gin.Context) {
		circuitBreaker, exists := s.CircuitBreakers[targetServiceName]
		if !exists {
			circuitBreaker = circuitbreaker.NewCircuitBreaker(targetServiceName, 5, 30*time.Second)
			s.CircuitBreakers[targetServiceName] = circuitBreaker
		}

		if !circuitBreaker.IsAllowed() {
			s.Logger.Error("Circuit open for service %s, request rejected", targetServiceName)
			c.JSON(http.StatusServiceUnavailable, gin.H{"error": "Service temporarily unavailable"})
			return
		}

		serviceURL := s.Discovery.GetServiceURL(targetServiceName)
		if serviceURL == "" {
			s.Logger.Error("Service not found: %s", targetServiceName)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Service unavailable"})
			return
		}

		startTime := time.Now()
		s.Metrics.RecordRequest(targetServiceName)

		var targetPath string
		if targetServiceName == "EXECUTOR-SVC" {
			path := c.Request.URL.Path
			segments := strings.Split(path, "/")
			if len(segments) >= 3 {
				targetPath = "/" + segments[len(segments)-1]

				if len(segments) >= 4 && (segments[len(segments)-2] == "result" || segments[len(segments)-2] == "cleanup") {
					targetPath = "/" + segments[len(segments)-2] + "/" + segments[len(segments)-1]
				}
			}
		} else {
			targetPath = c.Request.URL.Path
		}

		remote, err := url.Parse(serviceURL)
		if err != nil {
			s.Logger.Error("Failed to parse target URL: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
			s.Metrics.RecordError(targetServiceName)
			return
		}

		proxy := httputil.NewSingleHostReverseProxy(remote)

		originalHandler := proxy.Director
		proxy.Director = func(req *http.Request) {
			originalHandler(req)

			req.URL.Scheme = remote.Scheme
			req.URL.Host = remote.Host

			if targetServiceName == "EXECUTOR-SVC" {
				req.URL.Path = targetPath
			} else {
				req.URL.Path = c.Request.URL.Path
			}

			req.URL.RawQuery = c.Request.URL.RawQuery

			for key, values := range c.Request.Header {
				for _, value := range values {
					req.Header.Add(key, value)
				}
			}

			s.Logger.Info("Proxying request: %s %s -> %s%s",
				req.Method,
				c.Request.URL.Path,
				remote,
				req.URL.Path)
		}

		proxy.ErrorHandler = func(rw http.ResponseWriter, req *http.Request, err error) {
			s.Metrics.RecordError(targetServiceName)
			s.Logger.Error("Proxy error: %v", err)
			circuitBreaker.Failure()
			rw.WriteHeader(http.StatusBadGateway)
			_, _ = rw.Write([]byte("Service unavailable"))
		}

		proxy.ServeHTTP(c.Writer, c.Request)

		duration := time.Since(startTime)
		s.Metrics.RecordResponseTime(targetServiceName, duration)

		if c.Writer.Status() >= 500 {
			circuitBreaker.Failure()
			s.Metrics.RecordError(targetServiceName)
		} else {
			circuitBreaker.Success()
		}
	}
}

func (s *Server) Run() error {
	return s.Router.Run(fmt.Sprintf(":%d", s.Config.Port))
}
