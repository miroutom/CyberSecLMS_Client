package middleware

import (
	"fmt"
	"time"

	"github.com/gin-gonic/gin"

	"lmsmodule/api-gateway/pkg/logger"
)

func LoggerMiddleware(logger *logger.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		startTime := time.Now()
		c.Next()
		endTime := time.Now()
		latency := endTime.Sub(startTime)

		logger.Info(fmt.Sprintf("%s %s %s %d %s",
			c.Request.Method,
			c.Request.URL.Path,
			c.ClientIP(),
			c.Writer.Status(),
			latency,
		))
	}
}
