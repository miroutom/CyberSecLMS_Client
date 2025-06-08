package ut

import (
	_ "lmsmodule/api-gateway/internal/circuitbreaker"
	_ "net/http"
	"net/http/httptest"
	_ "testing"
	_ "time"

	_ "github.com/gin-gonic/gin"
	_ "github.com/stretchr/testify/assert"
	_ "lmsmodule/api-gateway/internal/api"
	_ "lmsmodule/api-gateway/pkg/logger"
)

type ResponseRecorderWithCloseNotify struct {
	*httptest.ResponseRecorder
}

func (rr *ResponseRecorderWithCloseNotify) CloseNotify() <-chan bool {
	return nil
}

type MockServiceDiscovery struct {
	GetServiceURLFn func(serviceName string) (string, error)
}

func (m *MockServiceDiscovery) GetServiceURL(serviceName string) (string, error) {
	return m.GetServiceURLFn(serviceName)
}

//func TestProxyRequest(t *testing.T) {
//	logger := logger.NewLogger("debug")
//
//	mockDiscovery := &MockServiceDiscovery{
//		GetServiceURLFn: func(serviceName string) (string, error) {
//			return "http://mocked-service", nil
//		},
//	}
//
//	server := &api.Server{
//		Router: gin.Default(),
//		Logger: logger,
//		HttpClient: &http.Client{
//			Timeout: 30 * time.Second,
//		},
//		Discovery:       mockDiscovery,
//		CircuitBreakers: make(map[string]*circuitbreaker.CircuitBreaker),
//	}
//
//	localServer := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
//		w.WriteHeader(http.StatusOK)
//		w.Write([]byte("OK"))
//	}))
//	defer localServer.Close()
//
//	w := httptest.NewRecorder()
//	c, _ := gin.CreateTestContext(w)
//	c.Request, _ = http.NewRequest("GET", "/test", nil)
//
//	proxyHandler := server.ProxyRequest("test-service")
//	proxyHandler(c)
//
//	assert.Equal(t, http.StatusOK, w.Code)
//	assert.Equal(t, "OK", w.Body.String())
//}
