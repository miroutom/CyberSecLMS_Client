package handlers

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/backend-svc/models"
	"net/http"
	"strconv"
)

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

// @Summary Complete assignment
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Param assignment_id path int true "Assignment ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/assignments/{assignment_id}/complete [post]
func CompleteAssignment(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	assignmentID, err := strconv.Atoi(c.Param("assignment_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid assignment ID"})
		return
	}

	err = Store.CompleteAssignment(userID, assignmentID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Assignment completed successfully"})
}
