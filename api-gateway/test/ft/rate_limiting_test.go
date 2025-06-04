package ft

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"lmsmodule/api-gateway/internal/middleware"
)

func TestRateLimiterMiddleware(t *testing.T) {
	router := gin.Default()

	api := router.Group("/api")
	api.Use(middleware.RateLimiterMiddleware())
	{
		api.GET("/test", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{"status": "ok"})
		})
	}

	go func() {
		if err := router.Run(":8081"); err != nil {
			t.Logf("Failed to start server: %v", err)
		}
	}()

	time.Sleep(1 * time.Second)

	client := &http.Client{}

	for i := 0; i < 100; i++ {
		resp, err := client.Get("http://localhost:8081/api/test")
		if err != nil {
			t.Fatal(err)
		}
		defer resp.Body.Close()
		assert.Equal(t, http.StatusOK, resp.StatusCode)
		fmt.Printf("Request %d: %d\n", i+1, resp.StatusCode)
	}

	resp, err := client.Get("http://localhost:8081/api/test")
	if err != nil {
		t.Fatal(err)
	}
	defer resp.Body.Close()
	assert.Equal(t, http.StatusTooManyRequests, resp.StatusCode)
	fmt.Printf("Final request: %d\n", resp.StatusCode)
}
