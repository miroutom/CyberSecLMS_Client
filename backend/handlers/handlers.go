package handlers

import (
	"lmsmodule/backend/storage"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

func GetCourses(c *gin.Context) {
	c.JSON(http.StatusOK, storage.Courses)
}

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

func GetUserProgress(c *gin.Context) {
	userID, _ := strconv.Atoi(c.Param("user_id"))
	if progress, exists := storage.UserProgress[userID]; exists {
		c.JSON(http.StatusOK, progress)
	} else {
		c.JSON(http.StatusNotFound, gin.H{"message": "User not found"})
	}
}

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
