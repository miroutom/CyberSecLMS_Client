package api

import (
	"github.com/gin-gonic/gin"

	"lmsmodule/api-gateway/internal/middleware"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type ProxyHandlerFunc func(string) gin.HandlerFunc

func setupRoutes(router *gin.Engine, config *utils.Config, logger *logger.Logger, proxyHandler ProxyHandlerFunc) {
	public := router.Group("/api")
	{
		public.Any("/register", proxyHandler(config.AuthService.URL))
		public.Any("/login", proxyHandler(config.AuthService.URL))
		public.Any("/verify-otp", proxyHandler(config.AuthService.URL))
		public.Any("/health", proxyHandler(config.AuthService.URL))
	}

	api := router.Group("/api")
	api.Use(middleware.RateLimiterMiddleware())
	{
		api.Any("/courses", proxyHandler(config.CourseService.URL))
		api.Any("/courses/:id", proxyHandler(config.CourseService.URL))

		api.Any("/progress/:user_id", proxyHandler(config.CourseService.URL))
		api.Any("/progress/:user_id/assignments/:assignment_id/complete", proxyHandler(config.CourseService.URL))

		api.Any("/profile", proxyHandler(config.AuthService.URL))

		account := api.Group("/account")
		{
			account.Any("/2fa/enable", proxyHandler(config.AuthService.URL))
		}

		admin := api.Group("/admin")
		{
			admin.Any("/reload-templates", proxyHandler(config.AuthService.URL))

			admin.Any("/users", proxyHandler(config.AuthService.URL))
			admin.Any("/users/:id", proxyHandler(config.AuthService.URL))
			admin.Any("/users/by-role", proxyHandler(config.AuthService.URL))
			admin.Any("/users/search", proxyHandler(config.AuthService.URL))
			admin.Any("/users/:id/status", proxyHandler(config.AuthService.URL))
			admin.Any("/users/:id/promote", proxyHandler(config.AuthService.URL))
			admin.Any("/users/:id/demote", proxyHandler(config.AuthService.URL))
		}
	}
}
