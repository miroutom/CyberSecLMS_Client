package ut

import (
	"bytes"
	"encoding/json"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
)

func setupTestRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage
	return router
}

func TestRegisterHandler(t *testing.T) {
	t.Run("Successful registration", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/register", handlers.RegisterHandler)

		registerReq := models.RegisterRequest{
			Username: "newuser",
			Password: "password123",
			Email:    "newuser@example.com",
			FullName: "New User",
		}
		jsonValue, _ := json.Marshal(registerReq)
		req, _ := http.NewRequest("POST", "/register", bytes.NewBuffer(jsonValue))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusCreated, w.Code)
		var response models.RegisterResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.NotEmpty(t, response.Token)
		assert.Equal(t, "User created successfully", response.Message)
	})

	t.Run("Invalid request", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/register", handlers.RegisterHandler)

		req, _ := http.NewRequest("POST", "/register", bytes.NewBuffer([]byte("invalid json")))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}

func TestLoginHandler(t *testing.T) {
	t.Run("Invalid credentials", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/login", handlers.LoginHandler)

		loginReq := models.LoginRequest{
			Username: "nonexistentuser",
			Password: "password123",
		}
		jsonValue, _ := json.Marshal(loginReq)
		req, _ := http.NewRequest("POST", "/login", bytes.NewBuffer(jsonValue))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusUnauthorized, w.Code)
	})
}

func TestVerifyOTPHandler(t *testing.T) {
	t.Run("Invalid temp token", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/verify-otp", handlers.VerifyOTPHandler)

		otpReq := models.VerifyOTPRequest{
			TempToken: "invalid.token.here",
			OTP:       "123456",
		}
		jsonValue, _ := json.Marshal(otpReq)
		req, _ := http.NewRequest("POST", "/verify-otp", bytes.NewBuffer(jsonValue))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusUnauthorized, w.Code)
	})
}

func TestEnable2FAHandler(t *testing.T) {
	t.Run("Unauthorized", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/enable-2fa", handlers.Enable2FAHandler)

		enable2FAReq := models.Enable2FARequest{
			OTP: "123456",
		}
		jsonValue, _ := json.Marshal(enable2FAReq)
		req, _ := http.NewRequest("POST", "/enable-2fa", bytes.NewBuffer(jsonValue))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusUnauthorized, w.Code)
	})

	t.Run("With authorization", func(t *testing.T) {
		router := setupTestRouter()
		router.POST("/enable-2fa", func(c *gin.Context) {
			c.Set("userID", 2)
			handlers.Enable2FAHandler(c)
		})

		enable2FAReq := models.Enable2FARequest{
			OTP: "invalid",
		}
		jsonValue, _ := json.Marshal(enable2FAReq)
		req, _ := http.NewRequest("POST", "/enable-2fa", bytes.NewBuffer(jsonValue))

		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}
