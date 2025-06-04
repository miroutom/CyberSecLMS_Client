package ft

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestRouting(t *testing.T) {
	router := gin.Default()

	api := router.Group("/api")
	{
		api.GET("/courses", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{"status": "ok"})
		})

		api.GET("/profile", func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{"status": "ok"})
		})
	}

	go func() {
		if err := router.Run(":8082"); err != nil {
			t.Logf("Failed to start server: %v", err)
		}
	}()

	time.Sleep(1 * time.Second)

	client := &http.Client{}

	resp, err := client.Get("http://localhost:8082/api/courses")
	if err != nil {
		t.Fatal(err)
	}
	defer resp.Body.Close()
	assert.Equal(t, http.StatusOK, resp.StatusCode)

	resp, err = client.Get("http://localhost:8082/api/profile")
	if err != nil {
		t.Fatal(err)
	}
	defer resp.Body.Close()
	assert.Equal(t, http.StatusOK, resp.StatusCode)
}
