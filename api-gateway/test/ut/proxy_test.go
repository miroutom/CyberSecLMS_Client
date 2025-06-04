package ut

import (
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"lmsmodule/api-gateway/internal/api"
	"lmsmodule/api-gateway/pkg/logger"
)

type ResponseRecorderWithCloseNotify struct {
	*httptest.ResponseRecorder
}

func (rr *ResponseRecorderWithCloseNotify) CloseNotify() <-chan bool {
	return nil
}

func TestProxyRequest(t *testing.T) {
	logger := logger.NewLogger("debug")
	httpClient := &http.Client{
		Timeout: time.Duration(30) * time.Second,
	}
	server := &api.Server{
		Router:     gin.Default(),
		Logger:     logger,
		HttpClient: httpClient,
	}

	localServer := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("OK"))
	}))
	defer localServer.Close()

	w := &ResponseRecorderWithCloseNotify{httptest.NewRecorder()}
	c, _ := gin.CreateTestContext(w)
	c.Request, _ = http.NewRequest("GET", "/test", nil)

	proxyHandler := server.ProxyRequest(localServer.URL)
	proxyHandler(c)

	assert.Equal(t, http.StatusOK, w.Code)
}
