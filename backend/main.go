package main

import (
	"database/sql"
	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
	"lmsmodule/backend/handlers"
	"log"
	"os"
	"time"
)

// @title LMS API
// @version 1.0
// @description API for Learning Management System
// @host localhost:8080
// @BasePath /api
func main() {
	dsn := "lms_user:Ept@Meny@8NeSpros1l1@tcp(localhost:3306)/lms_db?parseTime=true"
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal("Database connection error:", err)
	}
	defer db.Close()

	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(25)
	db.SetConnMaxLifetime(5 * time.Minute)

	if err := db.Ping(); err != nil {
		log.Fatal("Database ping failed:", err)
	}

	handlers.SetDB(db)
	r := gin.Default()

	api := r.Group("/api")
	{
		// @Summary User login
		// @Router /login [post]
		api.POST("/login", handlers.LoginHandler)

		// @Summary Get all courses
		// @Router /courses [get]
		api.GET("/courses", handlers.GetCourses)

		// @Summary Get course by ID
		// @Router /courses/{id} [get]
		api.GET("/courses/:id", handlers.GetCourseByID)

		// @Summary Get user progress
		// @Router /progress/{user_id} [get]
		api.GET("/progress/:user_id", handlers.GetUserProgress)

		// @Summary Complete assignment
		// @Router /progress/{user_id}/assignments/{assignment_id}/complete [post]
		api.POST("/progress/:user_id/assignments/:assignment_id/complete", handlers.CompleteAssignment)
	}

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	log.Printf("Server starting on port %s", port)
	if err := r.Run(":" + port); err != nil {
		log.Fatal("Server failed:", err)
	}
}
