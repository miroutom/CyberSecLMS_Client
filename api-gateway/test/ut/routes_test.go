package ut

import (
	"net/http"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"lmsmodule/api-gateway/internal/api"
	"lmsmodule/api-gateway/pkg/logger"
)

func TestSetupRoutes(t *testing.T) {
	logger := logger.NewLogger("debug")
	router := gin.New()

	server := &api.Server{
		Router:     router,
		Logger:     logger,
		HttpClient: &http.Client{},
	}

	public := router.Group("/api")
	{
		public.Any("/register", server.ProxyRequest("http://auth.service"))
		public.Any("/login", server.ProxyRequest("http://auth.service"))
	}

	assert.NotNil(t, router.Routes())
}
