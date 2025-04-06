package main

import (
	"github.com/gin-gonic/gin"
	_ "github.com/swaggo/files"
	_ "github.com/swaggo/gin-swagger"
	handlers2 "lmsmodule/backend/handlers"
)

// @title LMS API
// @version 1.0
// @description API for Learning Management System (LMS).
// @contact.name Support Team
// @contact.url http://www.example.com/support
// @contact.email support@example.com
// @host localhost:8080
// @scheme http

func main() {
	r := gin.Default()

	// @Summary Logs in the user
	// @Description Handler for user login, returns JWT token on successful authentication.
	// @ID login
	// @Produce json
	// @Param body body handlers2.LoginRequest true "Login data"
	// @Success 200 {object} gin.H "Successful login"
	// @Failure 400 {object} gin.H "Invalid request"
	// @Failure 401 {object} gin.H "Invalid credentials"
	// @Router /login [post]
	r.POST("/login", handlers2.LoginHandler)

	// @Summary Gets all courses
	// @Description Retrieves the list of all available courses.
	// @ID get-all-courses
	// @Produce json
	// @Success 200 {object} []handlers2.Course "List of courses"
	// @Router /courses [get]
	r.GET("/courses", handlers2.GetCourses)

	// @Summary Gets a course by ID
	// @Description Retrieves a specific course by its unique ID.
	// @ID get-course-by-id
	// @Produce json
	// @Param id path int true "Course ID"
	// @Success 200 {object} handlers2.Course "Course details"
	// @Failure 404 {object} gin.H "Course not found"
	// @Router /courses/{id} [get]
	r.GET("/courses/:id", handlers2.GetCourseByID)

	// @Summary Gets user progress
	// @Description Retrieves the progress of a user.
	// @ID get-user-progress
	// @Produce json
	// @Param user_id path int true "User ID"
	// @Success 200 {object} handlers2.UserProgress "User progress"
	// @Failure 404 {object} gin.H "User not found"
	// @Router /progress/{user_id} [get]
	r.GET("/progress/:user_id", handlers2.GetUserProgress)

	// @Summary Completes an assignment
	// @Description Marks an assignment as completed for a user.
	// @ID complete-assignment
	// @Produce json
	// @Param user_id path int true "User ID"
	// @Param assignment_id path int true "Assignment ID"
	// @Success 200 {object} gin.H "Assignment marked as completed"
	// @Failure 404 {object} gin.H "User not found"
	// @Router /progress/{user_id}/assignments/{assignment_id}/complete [post]
	r.POST("/progress/:user_id/:assignment_id/complete", handlers2.CompleteAssignment)

	r.Run(":8080") // Starts the service on port 8080
}
