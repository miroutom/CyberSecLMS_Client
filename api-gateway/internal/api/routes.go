package api

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/api-gateway/internal/middleware"
)

type ProxyHandlerFunc func(string) gin.HandlerFunc

func SetupRoutes(router *gin.Engine, proxyHandler ProxyHandlerFunc) {
	public := router.Group("/api")
	{
		public.Any("/register", proxyHandler("BACKEND-SERVICE"))
		public.Any("/login", proxyHandler("BACKEND-SERVICE"))
		public.Any("/verify-otp", proxyHandler("BACKEND-SERVICE"))
		public.Any("/forgot-password", proxyHandler("BACKEND-SERVICE"))
		public.Any("/reset-password", proxyHandler("BACKEND-SERVICE"))
		public.Any("/health", proxyHandler("BACKEND-SERVICE"))
	}

	api := router.Group("/api")
	api.Use(middleware.RateLimiterMiddleware())
	{
		api.Any("/courses", proxyHandler("BACKEND-SERVICE"))
		api.Any("/courses/:id", proxyHandler("BACKEND-SERVICE"))
		api.GET("/courses/:id/tasks/:task_id", proxyHandler("BACKEND-SERVICE"))

		api.Any("/progress/:user_id", proxyHandler("BACKEND-SERVICE"))
		api.Any("/progress/:user_id/tasks/:task_id/complete", proxyHandler("BACKEND-SERVICE"))

		api.Any("/profile", proxyHandler("BACKEND-SERVICE"))

		account := api.Group("/account")
		{
			account.Any("/2fa/enable", proxyHandler("BACKEND-SERVICE"))
			account.Any("/profile/image", proxyHandler("BACKEND-SERVICE"))
			account.Any("/change-password", proxyHandler("BACKEND-SERVICE"))
			account.Any("/delete", proxyHandler("BACKEND-SERVICE"))
			account.Any("/delete/confirm", proxyHandler("BACKEND-SERVICE"))
		}

		analytics := api.Group("/analytics")
		{
			analytics.GET("/users/:user_id/statistics", proxyHandler("BACKEND-SERVICE"))
		}

		teacher := api.Group("/teacher")
		{
			teacher.POST("/courses", proxyHandler("BACKEND-SERVICE"))
			teacher.PUT("/courses", proxyHandler("BACKEND-SERVICE"))
			teacher.DELETE("/courses", proxyHandler("BACKEND-SERVICE"))
			teacher.POST("/courses/:course_id/tasks", proxyHandler("BACKEND-SERVICE"))
			teacher.PUT("/courses/:course_id/tasks/:task_id", proxyHandler("BACKEND-SERVICE"))
			teacher.DELETE("/courses/:course_id/tasks/:task_id", proxyHandler("BACKEND-SERVICE"))
			teacher.GET("/courses/:id/statistics", proxyHandler("BACKEND-SERVICE"))
		}

		admin := api.Group("/admin")
		{
			admin.POST("/reload-templates", proxyHandler("BACKEND-SERVICE"))

			admin.GET("/users", proxyHandler("BACKEND-SERVICE"))
			admin.GET("/users/:id", proxyHandler("BACKEND-SERVICE"))
			admin.GET("/users/by-role", proxyHandler("BACKEND-SERVICE"))
			admin.GET("/users/search", proxyHandler("BACKEND-SERVICE"))
			admin.PUT("/users/:id/status", proxyHandler("BACKEND-SERVICE"))
			admin.POST("/users/:id/promote", proxyHandler("BACKEND-SERVICE"))
			admin.POST("/users/:id/demote", proxyHandler("BACKEND-SERVICE"))
			admin.GET("/analytics/courses/:course_id/statistics", proxyHandler("BACKEND-SERVICE"))
		}

		executor := api.Group("/executor")
		{
			executor.POST("/prewarm", proxyHandler("EXECUTOR-SVC"))
			executor.POST("/execute", proxyHandler("EXECUTOR-SVC"))
			executor.POST("/execute_pytest", proxyHandler("EXECUTOR-SVC"))
			executor.GET("/result/:session_id", proxyHandler("EXECUTOR-SVC"))
			executor.POST("/cleanup/:session_id", proxyHandler("EXECUTOR-SVC"))
		}
	}

	router.Static("/uploads", "/uploads")
}
