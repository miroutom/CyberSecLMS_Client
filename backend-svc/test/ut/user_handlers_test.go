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

func TestGetUserProfile(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/profile", func(c *gin.Context) {
		c.Set("userID", 1)
		handlers.GetUserProfile(c)
	})

	req, _ := http.NewRequest("GET", "/profile", nil)
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var user models.User
	json.Unmarshal(w.Body.Bytes(), &user)
	assert.Equal(t, 1, user.ID)
	assert.Empty(t, user.PasswordHash)
	assert.Empty(t, user.TOTPSecret)
}

func TestUpdateUserProfile(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.PUT("/profile", func(c *gin.Context) {
		c.Set("userID", 1)
		handlers.UpdateUserProfile(c)
	})

	t.Run("Valid request", func(t *testing.T) {
		updateReq := models.UpdateProfileRequest{
			Email:    "new@example.com",
			FullName: "New Name",
		}
		jsonValue, _ := json.Marshal(updateReq)
		req, _ := http.NewRequest("PUT", "/profile", bytes.NewBuffer(jsonValue))
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var response map[string]string
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Equal(t, "Profile updated successfully", response["message"])
	})

	t.Run("Invalid request", func(t *testing.T) {
		req, _ := http.NewRequest("PUT", "/profile", bytes.NewBuffer([]byte("invalid json")))
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}

func TestInitDeleteAccount(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.POST("/account/delete", func(c *gin.Context) {
		c.Set("userID", 1)
		handlers.InitDeleteAccount(c)
	})

	t.Run("Invalid request", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/account/delete", bytes.NewBuffer([]byte("invalid json")))
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}

func TestConfirmDeleteAccount(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.POST("/account/delete/confirm", func(c *gin.Context) {
		c.Set("userID", 1)
		handlers.ConfirmDeleteAccount(c)
	})

	t.Run("Valid request", func(t *testing.T) {
		mockStorage.SaveOTPCode(1, "123456")

		confirmReq := models.DeleteAccountConfirmRequest{
			Code: "123456",
		}
		jsonValue, _ := json.Marshal(confirmReq)
		req, _ := http.NewRequest("POST", "/account/delete/confirm", bytes.NewBuffer(jsonValue))
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var response models.SuccessResponse
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Contains(t, response.Message, "successfully deleted")
	})
}

func TestGetUserByID(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/admin/users/:id", handlers.GetUserByID)

	t.Run("Invalid user ID format", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/admin/users/invalid", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Equal(t, "Invalid user ID", response.Error)
	})
}

func TestGetAllUsers(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/admin/users", handlers.GetAllUsers)

	req, _ := http.NewRequest("GET", "/admin/users", nil)
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var users []models.User
	json.Unmarshal(w.Body.Bytes(), &users)

	for _, user := range users {
		assert.Empty(t, user.PasswordHash)
		assert.Empty(t, user.TOTPSecret)
	}
}

func TestGetUsersByRole(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/admin/users/by-role", handlers.GetUsersByRole)

	t.Run("Get admins", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/admin/users/by-role?is_admin=true", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var users []models.User
		json.Unmarshal(w.Body.Bytes(), &users)

		for _, user := range users {
			assert.Empty(t, user.PasswordHash)
			assert.Empty(t, user.TOTPSecret)
		}
	})

	t.Run("Get regular users", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/admin/users/by-role?is_admin=false", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)
	})
}

func TestSearchUsers(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/admin/users/search", handlers.SearchUsers)

	t.Run("Valid search", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/admin/users/search?query=admin", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var users []models.User
		json.Unmarshal(w.Body.Bytes(), &users)

		for _, user := range users {
			assert.Empty(t, user.PasswordHash)
			assert.Empty(t, user.TOTPSecret)
		}
	})

	t.Run("Empty query", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/admin/users/search?query=", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Equal(t, "Search query is required", response.Error)
	})
}

func TestUpdateUserStatus(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.PUT("/admin/users/:id/status", handlers.UpdateUserStatus)

	t.Run("Invalid user ID", func(t *testing.T) {
		statusReq := models.UpdateStatusRequest{
			IsActive: false,
		}
		jsonValue, _ := json.Marshal(statusReq)
		req, _ := http.NewRequest("PUT", "/admin/users/invalid/status", bytes.NewBuffer(jsonValue))
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}

func TestPromoteToAdmin(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.POST("/admin/users/:id/promote", handlers.PromoteToAdmin)

	t.Run("Valid request", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/admin/users/2/promote", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var response models.SuccessResponse
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Equal(t, "User promoted to admin successfully", response.Message)
	})

	t.Run("Invalid user ID", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/admin/users/invalid/promote", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)
	})
}

func TestUpdateProfileImageHandler(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.POST("/account/profile/image", func(c *gin.Context) {
		c.Set("userID", 1)
		handlers.UpdateProfileImageHandler(c)
	})

	t.Run("Missing file", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/account/profile/image", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		json.Unmarshal(w.Body.Bytes(), &response)
		assert.Contains(t, response.Error, "Image file required")
	})
}
