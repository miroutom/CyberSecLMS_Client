package ft

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestProxyRequest(t *testing.T) {
	router := gin.Default()

	public := router.Group("/api")
	{
		public.GET("/register", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{"status": "ok"})
		})
		public.GET("/login", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{"status": "ok"})
		})
	}

	go func() {
		if err := router.Run(":8080"); err != nil {
			t.Logf("Failed to start server: %v", err)
		}
	}()

	time.Sleep(1 * time.Second)

	client := &http.Client{}
	resp, err := client.Get("http://localhost:8080/api/register")
	if err != nil {
		t.Fatal(err)
	}
	defer resp.Body.Close()

	assert.Equal(t, http.StatusOK, resp.StatusCode)
}
