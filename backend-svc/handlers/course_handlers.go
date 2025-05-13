package handlers

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/backend-svc/models"
	"net/http"
	"strconv"
)

// GetCourses
// @Summary Get all courses
// @Tags Courses
// @Produce json
// @Success 200 {array} models.Course
// @Failure 500 {object} models.ErrorResponse
// @Router /courses [get]
func GetCourses(c *gin.Context) {
	courses, err := Store.GetCourses()
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, courses)
}

// GetCourseByID
// @Summary Get course by ID
// @Tags Courses
// @Produce json
// @Param id path int true "Course ID"
// @Success 200 {object} models.Course
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /courses/{id} [get]
func GetCourseByID(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	course, err := Store.GetCourseByID(id)
	if err != nil {
		c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Course not found"})
		return
	}

	c.JSON(http.StatusOK, course)
}

// GetUserProgress
// @Summary Get user progress
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.UserProgress
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id} [get]
func GetUserProgress(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	progress, err := Store.GetUserProgress(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, progress)
}

// CompleteTask
// @Summary Complete task
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Param task_id path int true "Task ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/tasks/{task_id}/complete [post]
func CompleteTask(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	taskID, err := strconv.Atoi(c.Param("task_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid task ID"})
		return
	}

	// Проверка прав доступа - пользователь может отмечать только свои задания
	currentUserID, exists := c.Get("userID")
	if !exists || userID != currentUserID.(int) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	err = Store.CompleteTask(userID, taskID)
	if err != nil {
		if err.Error() == "task not found" {
			c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Task not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to complete task"})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Task completed successfully"})
}
