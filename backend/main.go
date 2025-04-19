package main

import (
	"database/sql"
	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
	"github.com/golang-jwt/jwt/v5"
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
	// Инициализация подключения к БД
	dsn := "lms_user:Ept@Meny@8NeSpros1l1@tcp(localhost:3306)/lms_db?parseTime=true"
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal("Database connection error:", err)
	}
	defer db.Close()

	// Настройка пула соединений
	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(25)
	db.SetConnMaxLifetime(5 * time.Minute)

	if err := db.Ping(); err != nil {
		log.Fatal("Database ping failed:", err)
	}

	// Инициализация обработчиков
	handlers.SetDB(db)
	r := gin.Default()

	// Публичные роуты (не требующие аутентификации)
	public := r.Group("/api")
	{
		public.POST("/register", handlers.RegisterHandler)
		public.POST("/login", handlers.LoginHandler)
	}

	// Защищенные роуты (требующие JWT)
	api := r.Group("/api")
	api.Use(JWTAuthMiddleware())
	{
		api.GET("/courses", handlers.GetCourses)
		api.GET("/courses/:id", handlers.GetCourseByID)
		api.GET("/progress/:user_id", handlers.GetUserProgress)
		api.POST("/progress/:user_id/assignments/:assignment_id/complete", handlers.CompleteAssignment)
	}

	// Запуск сервера
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	log.Printf("Server starting on port %s", port)
	if err := r.Run("0.0.0.0:" + port); err != nil {
		log.Fatal("Server failed:", err)
	}
}

// JWTAuthMiddleware проверяет JWT токен в заголовке Authorization
func JWTAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Authorization header required"})
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			return []byte("your-secret-key"), nil // Замените на ваш секретный ключ
		})

		if err != nil || !token.Valid {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
			return
		}

		// Добавляем claims в контекст для использования в обработчиках
		if claims, ok := token.Claims.(jwt.MapClaims); ok {
			c.Set("userID", claims["sub"])
			c.Set("username", claims["username"])
		}

		c.Next()
	}
}
