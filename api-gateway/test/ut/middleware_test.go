package ut

import (
	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"lmsmodule/api-gateway/internal/middleware"
	"lmsmodule/api-gateway/pkg/logger"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestLoggerMiddleware(t *testing.T) {
	logger := logger.NewLogger("debug")
	mw := middleware.LoggerMiddleware(logger)

	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request, _ = http.NewRequest("GET", "/test", nil)

	mw(c)

	assert.Equal(t, http.StatusOK, w.Code)
}

func TestRateLimiterMiddleware(t *testing.T) {
	mw := middleware.RateLimiterMiddleware()

	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request, _ = http.NewRequest("GET", "/test", nil)

	mw(c)
	assert.Equal(t, http.StatusOK, w.Code)

	for i := 0; i < 100; i++ {
		mw(c)
	}

	mw(c)
	assert.Equal(t, http.StatusTooManyRequests, w.Code)
}

func TestSecurityHeadersMiddleware(t *testing.T) {
	mw := middleware.SecurityHeadersMiddleware()

	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request, _ = http.NewRequest("GET", "/test", nil)

	mw(c)

	assert.Equal(t, "nosniff", w.Header().Get("X-Content-Type-Options"))
	assert.Equal(t, "DENY", w.Header().Get("X-Frame-Options"))
	assert.Equal(t, "1; mode=block", w.Header().Get("X-XSS-Protection"))
	assert.Equal(t, "default-src 'self'", w.Header().Get("Content-Security-Policy"))

	assert.Equal(t, "*", w.Header().Get("Access-Control-Allow-Origin"))
	assert.Equal(t, "GET, POST, PUT, DELETE, OPTIONS", w.Header().Get("Access-Control-Allow-Methods"))
	assert.Equal(t, "Authorization, Content-Type", w.Header().Get("Access-Control-Allow-Headers"))
	assert.Equal(t, "86400", w.Header().Get("Access-Control-Max-Age"))
}
