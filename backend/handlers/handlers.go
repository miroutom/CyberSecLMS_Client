package handlers

import (
	"lmsmodule/backend/storage"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

// GetCourses
// @Summary Gets all courses
// @Description Retrieves the list of all available courses.
// @ID get-all-courses
// @Produce json
// @Success 200 {object} []models.Course "List of courses"
// @Router /courses [get]
func GetCourses(c *gin.Context) {
	c.JSON(http.StatusOK, storage.Courses)
}

// GetCourseByID
// @Summary Gets a course by ID
// @Description Retrieves a specific course by its unique ID.
// @ID get-course-by-id
// @Produce json
// @Param id path int true "Course ID"
// @Success 200 {object} models.Course "Course details"
// @Failure 404 {object} map[string]interface{} "Course not found"
// @Router /courses/{id} [get]
func GetCourseByID(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	for _, course := range storage.Courses {
		if course.ID == id {
			c.JSON(http.StatusOK, course)
			return
		}
	}
	c.JSON(http.StatusNotFound, gin.H{"message": "Course not found"})
}

// GetUserProgress
// @Summary Gets user progress
// @Description Retrieves the progress of a user.
// @ID get-user-progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.UserProgress "User progress"
// @Failure 404 {object} map[string]interface{} "User not found"
// @Router /progress/{user_id} [get]
func GetUserProgress(c *gin.Context) {
	userID, _ := strconv.Atoi(c.Param("user_id"))
	if progress, exists := storage.UserProgress[userID]; exists {
		c.JSON(http.StatusOK, progress)
	} else {
		c.JSON(http.StatusNotFound, gin.H{"message": "User not found"})
	}
}

// CompleteAssignment
// @Summary Completes an assignment
// @Description Marks an assignment as completed for a user.
// @ID complete-assignment
// @Produce json
// @Param user_id path int true "User ID"
// @Param assignment_id path int true "Assignment ID"
// @Success 200 {object} map[string]interface{} "Assignment marked as completed"
// @Failure 404 {object} map[string]interface{} "User not found"
// @Router /progress/{user_id}/assignments/{assignment_id}/complete [put]
func CompleteAssignment(c *gin.Context) {
	userID, _ := strconv.Atoi(c.Param("user_id"))
	assignmentID, _ := strconv.Atoi(c.Param("assignment_id"))

	if progress, exists := storage.UserProgress[userID]; exists {
		progress.Completed[assignmentID] = true
		c.JSON(http.StatusOK, gin.H{"message": "Assignment marked as completed"})
	} else {
		c.JSON(http.StatusNotFound, gin.H{"message": "User not found"})
	}
}
