package api

import (
	"fmt"
	"net/http"
	"net/http/httputil"
	"net/url"
	"strings"
	"time"

	"github.com/gin-gonic/gin"

	"lmsmodule/api-gateway/internal/discovery"
	"lmsmodule/api-gateway/internal/middleware"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type Server struct {
	Router     *gin.Engine
	Config     *utils.Config
	Logger     *logger.Logger
	HttpClient *http.Client
	Discovery  *discovery.ServiceDiscovery
}

func NewServer(config *utils.Config, logger *logger.Logger) *Server {
	router := gin.New()
	router.Use(middleware.LoggerMiddleware(logger))
	router.Use(gin.Recovery())
	router.Use(middleware.SecurityHeadersMiddleware())

	httpClient := &http.Client{
		Timeout: time.Duration(30) * time.Second,
	}

	serviceDiscovery, err := discovery.NewServiceDiscovery(config, logger)
	if err != nil {
		logger.Error("Failed to initialize service discovery: %v", err)
	}

	server := &Server{
		Router:     router,
		Config:     config,
		Logger:     logger,
		HttpClient: httpClient,
		Discovery:  serviceDiscovery,
	}

	server.setupRoutes()
	return server
}

func (s *Server) setupRoutes() {
	SetupRoutes(s.Router, s.Config, s.Logger, s.ProxyRequest)
}

func (s *Server) ProxyRequest(targetServiceName string) gin.HandlerFunc {
	return func(c *gin.Context) {
		serviceURL := s.Discovery.GetServiceURL(targetServiceName)
		if serviceURL == "" {
			s.Logger.Error("Service not found: %s", targetServiceName)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Service unavailable"})
			return
		}

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
			return
		}

		proxy := httputil.NewSingleHostReverseProxy(remote)
		proxy.Director = func(req *http.Request) {
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

		proxy.ServeHTTP(c.Writer, c.Request)
	}
}

func (s *Server) Run() error {
	return s.Router.Run(fmt.Sprintf(":%d", s.Config.Port))
}
