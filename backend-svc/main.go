package main

import (
	"database/sql"
	"fmt"
	"github.com/gin-gonic/gin"
	_ "github.com/go-sql-driver/mysql"
	"github.com/golang-jwt/jwt/v5"
	"github.com/hudl/fargo"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	_ "lmsmodule/backend-svc/docs"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"log"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"strings"
	"syscall"
	"time"
)

// @title LMS API
// @version 1.0
// @description API for Learning Management System
// @host localhost:8080
// @schemes https
// @BasePath /api
// @securityDefinitions.apikey BearerAuth
// @in header
// @name Authorization
func main() {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	log.Println("Starting LMS API server...")

	dsn := os.Getenv("DATABASE_DSN")
	if dsn == "" {
		user := os.Getenv("MYSQL_USER")
		password := os.Getenv("MYSQL_PASSWORD")
		database := os.Getenv("MYSQL_DATABASE")
		host := "db"
		port := "3306"

		if user == "" || password == "" || database == "" {
			log.Fatal("Database credentials not provided. Set MYSQL_USER, MYSQL_PASSWORD, MYSQL_DATABASE environment variables")
		}

		dsn = fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?parseTime=true", user, password, host, port, database)
	}

	handlers.JWTSecret = os.Getenv("JWT_SECRET")
	if handlers.JWTSecret == "" {
		handlers.JWTSecret = "mock_JWT"
		log.Fatal("JWT secret not provided")
	}

	handlers.TempJWTSecret = os.Getenv("TEMP_JWT_SECRET")
	if handlers.TempJWTSecret == "" {
		handlers.TempJWTSecret = "mock_JWT"
		log.Fatal("JWT secret not provided")
	}

	var useMockData = false

	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Printf("Database connection error: %v. Using mock data instead.", err)
		useMockData = true
	} else {
		err = db.Ping()
		if err != nil {
			log.Printf("Database ping failed: %v. Using mock data instead.", err)
			useMockData = true
		} else {
			db.SetMaxOpenConns(25)
			db.SetMaxIdleConns(25)
			db.SetConnMaxLifetime(5 * time.Minute)
			handlers.Db = db
			defer db.Close()
			log.Println("Successfully connected to database")
		}
	}

	if useMockData {
		log.Println("Using mock data storage")
		handlers.UseStorage(&storage.MockStorage{})
	} else {
		log.Println("Using database storage")
		handlers.UseStorage(&storage.DBStorage{DB: db})
	}

	eurekaURL := os.Getenv("EUREKA_URL")
	if eurekaURL == "" {
		eurekaURL = "http://discovery-server:8761/eureka/v2"
	}

	appName := os.Getenv("APP_NAME")
	if appName == "" {
		appName = "backend-svc"
	}

	instanceHost := os.Getenv("INSTANCE_IP")
	if instanceHost == "" {
		var err error
		instanceHost, err = os.Hostname()
		if err != nil {
			log.Printf("Error getting hostname: %v", err)
			instanceHost = "localhost"
		}
	}

	port := os.Getenv("PORT")
	if port == "" {
		port = "8081"
	}

	instancePort, err := strconv.Atoi(port)
	if err != nil {
		log.Printf("Error converting PORT: %v, using port 8081", err)
		instancePort = 8081
	}

	conn := fargo.NewConn(eurekaURL)
	conn.PollInterval = time.Second * 30

	metadata := fargo.InstanceMetadata{}

	instance := fargo.Instance{
		HostName:         instanceHost,
		Port:             instancePort,
		App:              appName,
		IPAddr:           instanceHost,
		VipAddress:       appName,
		SecureVipAddress: appName,
		DataCenterInfo: fargo.DataCenterInfo{
			Name:  "MyOwn",
			Class: "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
		},
		Status:           fargo.UP,
		Overriddenstatus: "UNKNOWN",
		LeaseInfo: fargo.LeaseInfo{
			RenewalIntervalInSecs: 30,
			DurationInSecs:        90,
		},
		Metadata:       metadata,
		HomePageUrl:    "http://" + instanceHost + ":" + port + "/",
		StatusPageUrl:  "http://" + instanceHost + ":" + port + "/info",
		HealthCheckUrl: "http://" + instanceHost + ":" + port + "/health",
	}

	err = conn.RegisterInstance(&instance)
	if err != nil {
		log.Printf("Error registering with Eureka: %v", err)
	} else {
		log.Println("Successfully registered with Eureka")

		ticker := time.NewTicker(time.Second * 30)
		go func() {
			for range ticker.C {
				err := conn.HeartBeatInstance(&instance)
				if err != nil {
					log.Printf("Heartbeat error: %v", err)
				}
			}
		}()

		go func() {
			sigChan := make(chan os.Signal, 1)
			signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
			<-sigChan

			log.Println("Deregistering from Eureka...")
			err = conn.DeregisterInstance(&instance)
			if err != nil {
				log.Printf("Error deregistering from Eureka: %v", err)
			} else {
				log.Println("Successfully deregistered from Eureka")
			}
			os.Exit(0)
		}()
	}

	r := gin.Default()
	r.Use(CORSMiddleware())
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	log.Println("Swagger documentation available at /swagger/index.html")

	public := r.Group("/api")
	{
		public.POST("/register", handlers.RegisterHandler)
		public.POST("/login", handlers.LoginHandler)
		public.POST("/verify-otp", handlers.VerifyOTPHandler)
		public.POST("/forgot-password", handlers.ForgotPassword)
		public.POST("/reset-password", handlers.ResetPassword)
		public.GET("/health", HealthCheckHandler)
	}

	api := r.Group("/api")
	api.Use(JWTAuthMiddleware())
	{
		api.GET("/courses", handlers.GetCourses)
		api.GET("/courses/:id", handlers.GetCourseByID)
		r.GET("/api/courses/:course_id/tasks/:task_id", handlers.GetTaskByID)
		api.GET("/progress/:user_id", handlers.GetUserProgress)
		api.POST("/progress/:user_id/tasks/:task_id/complete", handlers.CompleteTask)
		api.GET("/progress/:user_id/submissions", handlers.GetUserSubmissions)
		api.POST("/progress/:user_id/tasks/:task_id/submit", handlers.SubmitTaskWithAnswer)
		api.GET("/progress/:user_id/learning-path", handlers.GetUserLearningPath)

		api.GET("/profile", handlers.GetUserProfile)
		api.PUT("/profile", handlers.UpdateUserProfile)

		account := api.Group("/account")
		{
			account.POST("/2fa/enable", handlers.Enable2FAHandler)
			account.POST("/profile/image", handlers.UpdateProfileImageHandler)
			account.POST("/change-password", handlers.ChangePassword)
			account.POST("/delete", handlers.InitDeleteAccount)
			account.POST("/delete/confirm", handlers.ConfirmDeleteAccount)
		}

		analytics := api.Group("/analytics")
		{
			analytics.GET("/users/:user_id/statistics", handlers.GetUserStatistics)
		}

		teacher := api.Group("/teacher")
		teacher.Use(TeacherAuthMiddleware())
		{
			teacher.POST("/courses", handlers.CreateCourse)
			teacher.PUT("/courses", handlers.UpdateCourse)
			teacher.DELETE("/courses", handlers.DeleteCourse)
			teacher.POST("/courses/:course_id/tasks", handlers.CreateTask)
			teacher.PUT("/courses/:course_id/tasks/:task_id", handlers.UpdateTask)
			teacher.DELETE("/courses/:course_id/tasks/:task_id", handlers.DeleteTask)
			teacher.GET("/courses/:id/statistics", handlers.GetCourseStatistics)
		}

		admin := api.Group("/admin")
		admin.Use(AdminAuthMiddleware())
		{
			admin.POST("/reload-templates", handlers.ReloadTemplatesHandler)

			admin.GET("/users", handlers.GetAllUsers)
			admin.GET("/users/:id", handlers.GetUserByID)
			admin.GET("/users/by-role", handlers.GetUsersByRole)
			admin.GET("/users/search", handlers.SearchUsers)
			admin.PUT("/users/:id/status", handlers.UpdateUserStatus)
			admin.POST("/users/:id/promote", handlers.PromoteToAdmin)
			admin.POST("/users/:id/demote", handlers.DemoteFromAdmin)
			admin.GET("/analytics/courses/:course_id/statistics", handlers.GetCourseStatistics)
		}
	}

	r.Static("/uploads", "/uploads")

	log.Printf("Server starting on port %s", port)
	if err := r.Run("0.0.0.0:" + port); err != nil {
		log.Fatal("Server failed:", err)
	}
}

func JWTAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Authorization header required"})
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, jwt.ErrSignatureInvalid
			}
			return []byte(handlers.JWTSecret), nil
		})

		if err != nil {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token: " + err.Error()})
			return
		}

		if !token.Valid {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Token is not valid"})
			return
		}

		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token claims"})
			return
		}

		userID, ok := claims["sub"].(float64)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid user ID in token"})
			return
		}

		exp, ok := claims["exp"].(float64)
		if !ok || time.Now().Unix() > int64(exp) {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Token has expired"})
			return
		}

		c.Set("userID", int(userID))
		c.Next()
	}
}

func TeacherAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		userID, exists := c.Get("userID")
		if !exists {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
			return
		}

		isTeacher, err := handlers.CheckTeacherRights(userID.(int))
		if err != nil {
			c.AbortWithStatusJSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Error checking teacher rights: " + err.Error()})
			return
		}

		if !isTeacher {
			c.AbortWithStatusJSON(http.StatusForbidden, models.ErrorResponse{Error: "Teacher access required"})
			return
		}

		c.Next()
	}
}

func AdminAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		userID, exists := c.Get("userID")
		if !exists {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
			return
		}

		isAdmin, err := handlers.CheckAdminRights(userID.(int))
		if err != nil {
			c.AbortWithStatusJSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Error checking admin rights: " + err.Error()})
			return
		}

		if !isAdmin {
			c.AbortWithStatusJSON(http.StatusForbidden, models.ErrorResponse{Error: "Admin access required"})
			return
		}

		c.Next()
	}
}

func CORSMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE")

		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	}
}

func HealthCheckHandler(c *gin.Context) {
	dbStatus := "ok"
	if handlers.Db != nil {
		if err := handlers.Db.Ping(); err != nil {
			dbStatus = "error: " + err.Error()
		}
	} else {
		dbStatus = "using mock data"
	}

	c.JSON(http.StatusOK, gin.H{
		"status":   "ok",
		"time":     time.Now().Format(time.RFC3339),
		"database": dbStatus,
	})
}
