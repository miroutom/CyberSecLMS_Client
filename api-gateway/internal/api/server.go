package api

import (
	"fmt"
	"net/http"
	"net/http/httputil"
	"net/url"
	"time"

	"github.com/gin-gonic/gin"

	"lmsmodule/api-gateway/internal/middleware"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type Server struct {
	router     *gin.Engine
	config     *utils.Config
	logger     *logger.Logger
	httpClient *http.Client
}

func NewServer(config *utils.Config, logger *logger.Logger) *Server {
	router := gin.New()
	router.Use(middleware.LoggerMiddleware(logger))
	router.Use(gin.Recovery())

	httpClient := &http.Client{
		Timeout: time.Duration(30) * time.Second,
	}

	server := &Server{
		router:     router,
		config:     config,
		logger:     logger,
		httpClient: httpClient,
	}

	server.setupRoutes()
	return server
}

func (s *Server) setupRoutes() {
	setupRoutes(s.router, s.config, s.logger, s.proxyRequest)
}

func (s *Server) proxyRequest(targetURL string) gin.HandlerFunc {
	return func(c *gin.Context) {
		remote, err := url.Parse(targetURL)
		if err != nil {
			s.logger.Error("Failed to parse target URL: %v", err)
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
			return
		}

		proxy := httputil.NewSingleHostReverseProxy(remote)
		proxy.Director = func(req *http.Request) {
			req.URL.Scheme = remote.Scheme
			req.URL.Host = remote.Host
			req.URL.Path = c.Request.URL.Path
			req.URL.RawQuery = c.Request.URL.RawQuery

			for key, values := range c.Request.Header {
				for _, value := range values {
					req.Header.Add(key, value)
				}
			}

			s.logger.Info("Proxying request: %s %s -> %s%s",
				req.Method,
				c.Request.URL.Path,
				remote,
				req.URL.Path)
		}

		proxy.ServeHTTP(c.Writer, c.Request)
	}
}

func (s *Server) Run() error {
	return s.router.Run(fmt.Sprintf(":%d", s.config.Port))
}
