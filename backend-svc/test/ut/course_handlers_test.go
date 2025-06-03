package ut

import (
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

func TestGetCourses(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/courses", handlers.GetCourses)

	req, _ := http.NewRequest("GET", "/courses", nil)
	w := httptest.NewRecorder()
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var courses []models.Course
	err := json.Unmarshal(w.Body.Bytes(), &courses)
	assert.NoError(t, err)
	assert.NotEmpty(t, courses)
}

func TestGetCourseByID(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/courses/:id", handlers.GetCourseByID)

	t.Run("Valid course ID", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/courses/1", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var course models.Course
		err := json.Unmarshal(w.Body.Bytes(), &course)
		assert.NoError(t, err)
		assert.Equal(t, 1, course.ID)
		assert.Equal(t, "SQL Injection", course.VulnerabilityType)
	})

	t.Run("Invalid course ID format", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/courses/invalid", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Invalid course ID", response.Error)
	})

	t.Run("Course not found", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/courses/999", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusNotFound, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Course not found", response.Error)
	})
}

func TestGetUserProgress(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.GET("/progress/:user_id", handlers.GetUserProgress)

	t.Run("Valid user ID", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/progress/1", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var progress models.UserProgress
		err := json.Unmarshal(w.Body.Bytes(), &progress)
		assert.NoError(t, err)
		assert.Equal(t, 1, progress.UserID)
		assert.NotEmpty(t, progress.Completed)
	})

	t.Run("Invalid user ID format", func(t *testing.T) {
		req, _ := http.NewRequest("GET", "/progress/invalid", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Invalid user ID", response.Error)
	})
}

func TestCompleteTask(t *testing.T) {
	gin.SetMode(gin.TestMode)
	router := gin.New()
	mockStorage := new(storage.MockStorage)
	handlers.Store = mockStorage

	router.POST("/progress/:user_id/tasks/:task_id/complete", func(c *gin.Context) {
		userID := c.Param("user_id")
		c.Set("userID", 1)
		if userID == "1" {
			c.Set("userID", 1)
		} else if userID == "2" {
			c.Set("userID", 2)
		}
		handlers.CompleteTask(c)
	})

	t.Run("Successfully complete task", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/progress/1/tasks/2/complete", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusOK, w.Code)

		var response models.SuccessResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Task completed successfully", response.Message)
	})

	t.Run("Invalid user ID format", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/progress/invalid/tasks/2/complete", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Invalid user ID", response.Error)
	})

	t.Run("Invalid task ID format", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/progress/1/tasks/invalid/complete", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Invalid task ID", response.Error)
	})

	t.Run("Task not found", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/progress/1/tasks/999/complete", nil)
		w := httptest.NewRecorder()
		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusBadRequest, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Task not found", response.Error)
	})

	t.Run("Access denied", func(t *testing.T) {
		req, _ := http.NewRequest("POST", "/progress/2/tasks/2/complete", nil)
		w := httptest.NewRecorder()

		router := gin.New()
		router.POST("/progress/:user_id/tasks/:task_id/complete", func(c *gin.Context) {
			c.Set("userID", 1)
			handlers.CompleteTask(c)
		})

		router.ServeHTTP(w, req)

		assert.Equal(t, http.StatusForbidden, w.Code)

		var response models.ErrorResponse
		err := json.Unmarshal(w.Body.Bytes(), &response)
		if err != nil {
			t.Errorf("json.Unmarshal failed: %v", err)
		}
		assert.Equal(t, "Access denied", response.Error)
	})
}
