package main

import (
	"github.com/gin-gonic/gin"
	handlers2 "lmsmodule/backend/handlers"
)

func main() {
	r := gin.Default()

	r.POST("/login", handlers2.LoginHandler)
	r.GET("/courses", handlers2.GetCourses)
	r.GET("/courses/:id", handlers2.GetCourseByID)
	r.GET("/progress/:user_id", handlers2.GetUserProgress)
	r.POST("/progress/:user_id/:assignment_id/complete", handlers2.CompleteAssignment)

	r.Run(":8080") // Запуск сервиса на порту 8080
}
