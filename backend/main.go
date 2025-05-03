package main

import (
	"database/sql"
	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
	"github.com/golang-jwt/jwt/v5"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	_ "lmsmodule/backend/docs"
	"lmsmodule/backend/handlers"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

// @title LMS API
// @version 1.0
// @description API for Learning Management System
// @host localhost:8080
// @BasePath /api
// @securityDefinitions.apikey BearerAuth
// @in header
// @name Authorization
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

	handlers.Db = db
	r := gin.Default()

	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	public := r.Group("/api")
	{
		public.POST("/register", handlers.RegisterHandler)
		public.POST("/login", handlers.LoginHandler)
		public.POST("/verify-otp", handlers.VerifyOTPHandler)
	}

	api := r.Group("/api")
	api.Use(JWTAuthMiddleware())
	{
		api.GET("/courses", handlers.GetCourses)
		api.GET("/courses/:id", handlers.GetCourseByID)
		api.GET("/users/:id", handlers.GetUserByID)
		api.GET("/progress/:user_id", handlers.GetUserProgress)
		api.POST("/progress/:user_id/assignments/:assignment_id/complete", handlers.CompleteAssignment)

		account := api.Group("/account")
		{
			account.POST("/2fa/enable", handlers.Enable2FAHandler)
		}
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

func JWTAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Authorization header required"})
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, jwt.ErrSignatureInvalid
			}
			return []byte("your-secret-key"), nil
		})

		if err != nil || !token.Valid {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
			return
		}

		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token claims"})
			return
		}

		userID, ok := claims["sub"].(float64)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid user ID in token"})
			return
		}

		c.Set("userID", int(userID))
		if username, ok := claims["username"].(string); ok {
			c.Set("username", username)
		}

		c.Next()
	}
}
