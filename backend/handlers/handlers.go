package handlers

import (
	"database/sql"
	"lmsmodule/backend/models"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

var db *sql.DB

// SetDB устанавливает соединение с БД
func SetDB(database *sql.DB) {
	db = database
}

// GetCourses возвращает список всех курсов
// @Summary Get all courses
// @Produce json
// @Success 200 {array} models.Course
// @Router /courses [get]
func GetCourses(c *gin.Context) {
	rows, err := db.Query("SELECT id, title, description FROM courses")
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer rows.Close()

	var courses []models.Course
	for rows.Next() {
		var course models.Course
		if err := rows.Scan(&course.ID, &course.Title, &course.Description); err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
		courses = append(courses, course)
	}

	c.JSON(http.StatusOK, courses)
}

// GetCourseByID возвращает курс по ID
// @Summary Get course by ID
// @Produce json
// @Param id path int true "Course ID"
// @Success 200 {object} models.Course
// @Failure 404 {object} gin.H
// @Router /courses/{id} [get]
func GetCourseByID(c *gin.Context) {
	id, _ := strconv.Atoi(c.Param("id"))
	var course models.Course

	err := db.QueryRow("SELECT id, title, description FROM courses WHERE id = ?", id).
		Scan(&course.ID, &course.Title, &course.Description)

	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Course not found"})
		return
	}

	c.JSON(http.StatusOK, course)
}

// GetUserProgress возвращает прогресс пользователя
// @Summary Get user progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.UserProgress
// @Failure 404 {object} gin.H
// @Router /progress/{user_id} [get]
func GetUserProgress(c *gin.Context) {
	userID, _ := strconv.Atoi(c.Param("user_id"))

	rows, err := db.Query("SELECT assignment_id FROM user_progress WHERE user_id = ?", userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer rows.Close()

	completed := make(map[int]bool)
	for rows.Next() {
		var assignmentID int
		if err := rows.Scan(&assignmentID); err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
			return
		}
		completed[assignmentID] = true
	}

	c.JSON(http.StatusOK, models.UserProgress{
		UserID:    userID,
		Completed: completed,
	})
}

// CompleteAssignment отмечает задание как выполненное
// @Summary Complete assignment
// @Produce json
// @Param user_id path int true "User ID"
// @Param assignment_id path int true "Assignment ID"
// @Success 200 {object} gin.H
// @Failure 404 {object} gin.H
// @Router /progress/{user_id}/assignments/{assignment_id}/complete [put]
func CompleteAssignment(c *gin.Context) {
	userID := c.Param("user_id")
	assignmentID := c.Param("assignment_id")

	_, err := db.Exec(
		"INSERT INTO user_progress (user_id, assignment_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE assignment_id = VALUES(assignment_id)",
		userID, assignmentID,
	)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Assignment completed"})
}
