package ut

import (
	"encoding/json"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/stretchr/testify/assert"
)

func TestJWTAuthMiddleware(t *testing.T) {
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage
	handlers.JWTSecret = "test_jwt_secret"

	middleware := JWTAuthMiddleware()

	t.Run("Valid token", func(t *testing.T) {
		token := createTestToken(1, time.Now().Add(time.Hour).Unix())

		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request, _ = http.NewRequest("GET", "/", nil)
		c.Request.Header.Set("Authorization", "Bearer "+token)

		middleware(c)

		assert.False(t, c.IsAborted())
		userID, exists := c.Get("userID")
		assert.True(t, exists)
		assert.Equal(t, 1, userID)
	})

	t.Run("Missing authorization header", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request, _ = http.NewRequest("GET", "/", nil)

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, http.StatusUnauthorized, w.Code)

		var response models.ErrorResponse
		parseResponse(t, w.Body.Bytes(), &response)
		assert.Equal(t, "Authorization header required", response.Error)
	})

	t.Run("Invalid token format", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request, _ = http.NewRequest("GET", "/", nil)
		c.Request.Header.Set("Authorization", "Bearer invalid.token.format")

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, http.StatusUnauthorized, w.Code)
	})

	t.Run("Expired token", func(t *testing.T) {
		token := createTestToken(1, time.Now().Add(-time.Hour).Unix())

		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request, _ = http.NewRequest("GET", "/", nil)
		c.Request.Header.Set("Authorization", "Bearer "+token)

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, http.StatusUnauthorized, w.Code)
	})
}

func TestAdminAuthMiddleware(t *testing.T) {
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	middleware := AdminAuthMiddleware()

	t.Run("Admin user", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Set("userID", 1) // User 1 is admin in mock data

		middleware(c)

		assert.False(t, c.IsAborted())
	})

	t.Run("Non-admin user", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Set("userID", 2) // User 2 is not admin in mock data

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, http.StatusForbidden, w.Code)

		var response models.ErrorResponse
		parseResponse(t, w.Body.Bytes(), &response)
		assert.Equal(t, "Admin access required", response.Error)
	})

	t.Run("Missing user ID", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, http.StatusUnauthorized, w.Code)

		var response models.ErrorResponse
		parseResponse(t, w.Body.Bytes(), &response)
		assert.Equal(t, "Unauthorized", response.Error)
	})
}

func TestCORSMiddleware(t *testing.T) {
	middleware := CORSMiddleware()

	t.Run("OPTIONS request", func(t *testing.T) {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request, _ = http.NewRequest("OPTIONS", "/", nil)

		middleware(c)

		assert.True(t, c.IsAborted())
		assert.Equal(t, 204, w.Code)

		assert.Equal(t, "*", w.Header().Get("Access-Control-Allow-Origin"))
		assert.Equal(t, "true", w.Header().Get("Access-Control-Allow-Credentials"))
		assert.NotEmpty(t, w.Header().Get("Access-Control-Allow-Headers"))
		assert.NotEmpty(t, w.Header().Get("Access-Control-Allow-Methods"))
	})
}

func createTestToken(userID int, expiry int64) string {
	claims := jwt.MapClaims{
		"sub": float64(userID),
		"exp": float64(expiry),
		"iat": float64(time.Now().Unix()),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, _ := token.SignedString([]byte(handlers.JWTSecret))
	return tokenString
}

func parseResponse(t *testing.T, body []byte, target interface{}) {
	err := json.Unmarshal(body, target)
	if err != nil {
		t.Fatalf("Failed to parse response: %v", err)
	}
}

// Middleware implementations for testing
func JWTAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Authorization header required"})
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, jwt.ErrSignatureInvalid
			}
			return []byte(handlers.JWTSecret), nil
		})

		if err != nil {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token: " + err.Error()})
			return
		}

		if !token.Valid {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Token is not valid"})
			return
		}

		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token claims"})
			return
		}

		userID, ok := claims["sub"].(float64)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid user ID in token"})
			return
		}

		exp, ok := claims["exp"].(float64)
		if !ok || time.Now().Unix() > int64(exp) {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Token has expired"})
			return
		}

		c.Set("userID", int(userID))
		c.Next()
	}
}

func AdminAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		userID, exists := c.Get("userID")
		if !exists {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
			return
		}

		isAdmin, err := handlers.CheckAdminRights(userID.(int))
		if err != nil {
			c.AbortWithStatusJSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Error checking admin rights: " + err.Error()})
			return
		}

		if !isAdmin {
			c.AbortWithStatusJSON(http.StatusForbidden, models.ErrorResponse{Error: "Admin access required"})
			return
		}

		c.Next()
	}
}

func CORSMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE")

		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	}
}

func HealthCheckHandler(c *gin.Context) {
	dbStatus := "ok"
	if handlers.Db != nil {
		if err := handlers.Db.Ping(); err != nil {
			dbStatus = "error: " + err.Error()
		}
	} else {
		dbStatus = "using mock data"
	}

	c.JSON(http.StatusOK, gin.H{
		"status":   "ok",
		"time":     time.Now().Format(time.RFC3339),
		"database": dbStatus,
	})
}
