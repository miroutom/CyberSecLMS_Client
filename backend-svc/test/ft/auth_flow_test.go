package ft

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/go-resty/resty/v2"
	"github.com/golang-jwt/jwt/v5"
	_ "github.com/mattn/go-sqlite3"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"
)

type FunctionalTestSuite struct {
	suite.Suite
	router *gin.Engine
	db     *sql.DB
	token  string
	server *httptest.Server
	client *resty.Client
}

func (suite *FunctionalTestSuite) SetupSuite() {
	gin.SetMode(gin.TestMode)

	db, err := sql.Open("sqlite3", ":memory:")
	suite.Require().NoError(err)
	suite.db = db

	err = suite.createDatabaseSchema()
	suite.Require().NoError(err)

	err = suite.seedTestData()
	suite.Require().NoError(err)

	dbStorage := &storage.DBStorage{DB: db}
	handlers.Db = db
	handlers.UseStorage(dbStorage)
	handlers.JWTSecret = "test_jwt_secret_for_functional_tests"

	suite.router = gin.Default()
	suite.setupRoutes()

	suite.server = httptest.NewServer(suite.router)
	suite.client = resty.New()
	suite.client.SetBaseURL(suite.server.URL)
	suite.client.SetHeader("Content-Type", "application/json")
	suite.client.SetDebug(true)

	suite.generateToken()
}

func (suite *FunctionalTestSuite) TearDownSuite() {
	if suite.server != nil {
		suite.server.Close()
	}
	if suite.db != nil {
		suite.db.Close()
	}
}

func (suite *FunctionalTestSuite) createDatabaseSchema() error {
	_, err := suite.db.Exec(`
		CREATE TABLE users (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			username TEXT NOT NULL UNIQUE,
			password_hash TEXT NOT NULL,
			email TEXT NOT NULL UNIQUE,
			full_name TEXT,
			profile_image TEXT,
			totp_secret TEXT,
			is_2fa_enabled INTEGER DEFAULT 0,
			is_admin INTEGER DEFAULT 0,
			is_active INTEGER DEFAULT 1,
			is_teacher INTEGER DEFAULT 0,
			is_deleted INTEGER DEFAULT 0,
			last_login TIMESTAMP,
			otp_code TEXT,
			otp_expires_at TIMESTAMP,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		)
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		CREATE TABLE courses (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			vulnerability_type TEXT NOT NULL,
			description TEXT
		)
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		CREATE TABLE tasks (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			course_id INTEGER,
			title TEXT NOT NULL,
			description TEXT,
			difficulty TEXT,
			task_order INTEGER,
			points INTEGER DEFAULT 10,
			FOREIGN KEY (course_id) REFERENCES courses (id)
		)
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		CREATE TABLE user_progress (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			user_id INTEGER,
			task_id INTEGER,
			completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			status TEXT DEFAULT 'completed',
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			FOREIGN KEY (user_id) REFERENCES users (id),
			FOREIGN KEY (task_id) REFERENCES tasks (id),
			UNIQUE(user_id, task_id)
		)
	`)

	return err
}

func (suite *FunctionalTestSuite) seedTestData() error {
	adminPassHash, err := bcrypt.GenerateFromPassword([]byte("admin_password"), bcrypt.DefaultCost)
	if err != nil {
		return fmt.Errorf("generate admin password hash: %w", err)
	}

	userPassHash, err := bcrypt.GenerateFromPassword([]byte("user_password"), bcrypt.DefaultCost)
	if err != nil {
		return fmt.Errorf("generate user password hash: %w", err)
	}

	lastLoginTime := time.Now().Add(-1 * time.Hour)
	profileImage := "/default.png"
	totpSecret := "JBSWY3DPEHPK3PXP"
	otpCode := "123456"
	otpExpiresAt := time.Now().Add(5 * time.Minute)

	_, err = suite.db.Exec(`
        INSERT INTO users (
            username, 
            password_hash, 
            email, 
            full_name, 
            profile_image,
            totp_secret,
            is_2fa_enabled, 
            is_admin, 
            is_active,
            is_teacher,
            is_deleted,
            last_login,
            otp_code,
            otp_expires_at
        ) VALUES 
            ('admin', ?, 'admin@example.com', 'Admin User', ?, ?, 0, 1, 1, 1, 0, ?, ?, ?),
            ('user123', ?, 'user@example.com', 'Regular User', ?, ?, 0, 0, 1, 0, 0, ?, ?, ?)
    `,
		string(adminPassHash), profileImage, totpSecret, lastLoginTime, otpCode, otpExpiresAt,
		string(userPassHash), profileImage, totpSecret, lastLoginTime, otpCode, otpExpiresAt,
	)

	if err != nil {
		return fmt.Errorf("failed to seed users: %w", err)
	}

	_, err = suite.db.Exec(`
		INSERT INTO courses (id, vulnerability_type, description)
		VALUES 
			(1, 'SQL Injection', 'Learn about SQL injection vulnerabilities'),
			(2, 'XSS', 'Cross-site scripting attacks and prevention'),
			(3, 'CSRF', 'Cross-site request forgery attacks')
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		INSERT INTO tasks (id, course_id, title, description, difficulty, task_order, points)
		VALUES 
			(1, 1, 'Basics of SQL Injection', 'Understanding the fundamentals', 'easy', 1, 10),
			(2, 1, 'Advanced SQL Injection', 'More complex techniques', 'medium', 2, 20),
			(3, 2, 'XSS in Web Applications', 'Exploiting front-end vulnerabilities', 'medium', 1, 15),
			(4, 3, 'Understanding CSRF', 'Forging requests across sites', 'hard', 1, 25)
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		INSERT INTO user_progress (user_id, task_id, status)
		VALUES 
			(2, 1, 'completed'),
			(2, 2, 'completed')
	`)
	return err
}

func (suite *FunctionalTestSuite) setupRoutes() {
	public := suite.router.Group("/api")
	{
		public.POST("/register", handlers.RegisterHandler)
		public.POST("/login", handlers.LoginHandler)
		public.POST("/verify-otp", handlers.VerifyOTPHandler)
		public.GET("/courses", handlers.GetCourses)
		public.GET("/courses/:id", handlers.GetCourseByID)
	}

	api := suite.router.Group("/api")
	api.Use(suite.jwtAuthMiddleware())
	{
		api.GET("/profile", handlers.GetUserProfile)
		api.PUT("/profile", handlers.UpdateUserProfile)
		api.GET("/progress/:user_id", handlers.GetUserProgress)
		api.POST("/progress/:user_id/tasks/:task_id/complete", handlers.CompleteTask)
		api.GET("/progress/:user_id/submissions", handlers.GetUserSubmissions)
		api.POST("/progress/:user_id/tasks/:task_id/submit", handlers.SubmitTaskWithAnswer)
		api.GET("/progress/:user_id/learning-path", handlers.GetUserLearningPath)
	}

	admin := api.Group("/admin")
	admin.Use(suite.adminAuthMiddleware())
	{
		admin.GET("/users", handlers.GetAllUsers)
		admin.GET("/users/:id", handlers.GetUserByID)
		admin.GET("/analytics/courses/:course_id/statistics", handlers.GetCourseStatistics)
	}
}

func (suite *FunctionalTestSuite) generateToken() {
	// Генерируем токен напрямую без запроса к API
	// Это гарантированно работает и не зависит от корректности handler'а login
	userID := 2 // ID пользователя user123

	tokenExpiration := time.Now().Add(time.Hour * 24)
	claims := jwt.MapClaims{
		"sub": float64(userID),
		"exp": float64(tokenExpiration.Unix()),
		"iat": float64(time.Now().Unix()),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(handlers.JWTSecret))
	if err != nil {
		suite.T().Fatalf("Failed to generate JWT: %v", err)
		return
	}

	suite.token = tokenString
	suite.T().Logf("Generated token: %s", tokenString)
}

func (suite *FunctionalTestSuite) jwtAuthMiddleware() gin.HandlerFunc {
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

func (suite *FunctionalTestSuite) adminAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		userID, exists := c.Get("userID")
		if !exists {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
			return
		}

		var isAdmin int
		err := suite.db.QueryRow("SELECT is_admin FROM users WHERE id = ?", userID.(int)).Scan(&isAdmin)
		if err != nil || isAdmin != 1 {
			c.AbortWithStatusJSON(http.StatusForbidden, models.ErrorResponse{Error: "Admin access required"})
			return
		}

		c.Next()
	}
}

func (suite *FunctionalTestSuite) TestRegisterHandler() {
	t := suite.T()

	registerReq := models.RegisterRequest{
		Username:  "newuser",
		Password:  "password123",
		Email:     "new@example.com",
		FullName:  "New User",
		IsTeacher: false,
	}

	resp, err := suite.client.R().
		SetBody(registerReq).
		Post("/api/register")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusCreated, resp.StatusCode())

	var registerResp models.RegisterResponse
	err = json.Unmarshal(resp.Body(), &registerResp)
	assert.NoError(t, err)
	assert.NotEmpty(t, registerResp.Token)

	var count int
	var isTeacher bool
	err = suite.db.QueryRow("SELECT COUNT(*), is_teacher FROM users WHERE username = ?", "newuser").Scan(&count, &isTeacher)
	assert.NoError(t, err)
	assert.Equal(t, 1, count)
	assert.Equal(t, registerReq.IsTeacher, isTeacher)
}

func TestFunctionalTestSuite(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping functional tests in short mode")
	}
	suite.Run(t, new(FunctionalTestSuite))
}
