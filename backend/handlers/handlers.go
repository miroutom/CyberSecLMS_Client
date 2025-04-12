package handlers

import (
	"database/sql"
	"lmsmodule/backend/models"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

var db *sql.DB

// SetDB sets the global database connection.
func SetDB(database *sql.DB) {
	db = database
}

// GetCourses
// @Summary Gets all courses
// @Description Retrieves the list of all available courses.
// @ID get-all-courses
// @Produce json
// @Success 200 {object} []models.Course "List of courses"
// @Router /courses [get]
func GetCourses(c *gin.Context) {
	rows, err := db.Query("SELECT id, title, description FROM courses")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve courses"})
		return
	}
	defer rows.Close()

	var courses []models.Course
	for rows.Next() {
		var course models.Course
		err := rows.Scan(&course.ID, &course.Title, &course.Description)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse course data"})
			return
		}
		courses = append(courses, course)
	}

	c.JSON(http.StatusOK, courses)
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
	var course models.Course
	err := db.QueryRow("SELECT id, title, description FROM courses WHERE id = ?", id).Scan(&course.ID, &course.Title, &course.Description)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"message": "Course not found"})
		return
	}
	c.JSON(http.StatusOK, course)
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
	rows, err := db.Query("SELECT assignment_id FROM user_progress WHERE user_id = ?", userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to retrieve progress"})
		return
	}
	defer rows.Close()

	completed := make(map[int]bool)
	for rows.Next() {
		var assignmentID int
		_ = rows.Scan(&assignmentID)
		completed[assignmentID] = true
	}

	progress := models.UserProgress{
		UserID:    userID,
		Completed: completed,
	}

	c.JSON(http.StatusOK, progress)
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

	_, err := db.Exec("INSERT INTO user_progress (user_id, assignment_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE assignment_id = assignment_id", userID, assignmentID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to complete assignment"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Assignment marked as completed"})
}
