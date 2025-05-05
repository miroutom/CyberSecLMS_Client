package middleware

import (
	"net/http"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
)

type RateLimiter struct {
	sync.Mutex
	requests map[string][]time.Time
	limit    int
	window   time.Duration
}

func NewRateLimiter(limit int, window time.Duration) *RateLimiter {
	return &RateLimiter{
		requests: make(map[string][]time.Time),
		limit:    limit,
		window:   window,
	}
}

func RateLimiterMiddleware() gin.HandlerFunc {
	limiter := NewRateLimiter(100, time.Minute)

	return func(c *gin.Context) {
		ip := c.ClientIP()

		limiter.Lock()
		defer limiter.Unlock()

		now := time.Now()
		if _, exists := limiter.requests[ip]; !exists {
			limiter.requests[ip] = []time.Time{now}
			c.Next()
			return
		}

		var requests []time.Time
		for _, reqTime := range limiter.requests[ip] {
			if now.Sub(reqTime) < limiter.window {
				requests = append(requests, reqTime)
			}
		}

		if len(requests) >= limiter.limit {
			c.JSON(http.StatusTooManyRequests, gin.H{"error": "Too many requests"})
			c.Abort()
			return
		}

		limiter.requests[ip] = append(requests, now)
		c.Next()
	}
}
