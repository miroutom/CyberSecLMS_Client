package ft

import (
	"bytes"
	"database/sql"
	"encoding/json"
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/gin-gonic/gin"
	_ "github.com/mattn/go-sqlite3"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
	"golang.org/x/crypto/bcrypt"
)

type FunctionalTestSuite struct {
	suite.Suite
	router *gin.Engine
	db     *sql.DB
	token  string
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

	suite.router = gin.New()
	suite.setupRoutes()

	suite.token = suite.getAuthToken()
}

func (suite *FunctionalTestSuite) TearDownSuite() {
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
			last_login TIMESTAMP,
			otp_code TEXT,
			otp_expires_at TIMESTAMP
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
			FOREIGN KEY (user_id) REFERENCES users (id),
			FOREIGN KEY (task_id) REFERENCES tasks (id),
			UNIQUE(user_id, task_id)
		)
	`)

	return err
}

func (suite *FunctionalTestSuite) seedTestData() error {
	adminPassHash, _ := bcrypt.GenerateFromPassword([]byte("1"), bcrypt.DefaultCost)
	userPassHash, _ := bcrypt.GenerateFromPassword([]byte("1"), bcrypt.DefaultCost)

	lastLoginTime := time.Now().Add(-1 * time.Hour)
	profileImage := "/default.png"
	totpSecret := "JBSWY3DPEHPK3PXP"
	otpCode := "123456"
	otpExpiresAt := time.Now().Add(5 * time.Minute)

	_, err := suite.db.Exec(`
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
            last_login,
            otp_code,
            otp_expires_at
        ) VALUES 
            ('admin', ?, 'admin@example.com', 'Admin User', ?, ?, 0, 1, 1, ?, ?, ?),
            ('user123', ?, 'user@example.com', 'Regular User', ?, ?, 0, 0, 1, ?, ?, ?)
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
		INSERT INTO tasks (id, course_id, title, description, difficulty, task_order)
		VALUES 
			(1, 1, 'Basics of SQL Injection', 'Understanding the fundamentals', 'easy', 1),
			(2, 1, 'Advanced SQL Injection', 'More complex techniques', 'medium', 2),
			(3, 2, 'XSS in Web Applications', 'Exploiting front-end vulnerabilities', 'medium', 1),
			(4, 3, 'Understanding CSRF', 'Forging requests across sites', 'hard', 1)
	`)
	if err != nil {
		return err
	}

	_, err = suite.db.Exec(`
		INSERT INTO user_progress (user_id, task_id)
		VALUES 
			(2, 1),
			(2, 2)
	`)
	return err
}

func (suite *FunctionalTestSuite) setupRoutes() {
	suite.router.POST("/api/register", handlers.RegisterHandler)
	suite.router.POST("/api/login", handlers.LoginHandler)
	suite.router.POST("/api/verify-otp", handlers.VerifyOTPHandler)

	suite.router.GET("/api/courses", handlers.GetCourses)
	suite.router.GET("/api/courses/:id", handlers.GetCourseByID)

	api := suite.router.Group("/api")
	api.Use(JWTAuthMiddleware())
	{
		api.GET("/profile", handlers.GetUserProfile)
		api.PUT("/profile", handlers.UpdateUserProfile)
		api.GET("/progress/:user_id", handlers.GetUserProgress)
		api.POST("/progress/:user_id/tasks/:task_id/complete", handlers.CompleteTask)
	}

	admin := api.Group("/admin")
	admin.Use(AdminAuthMiddleware())
	{
		admin.GET("/users", handlers.GetAllUsers)
		admin.GET("/users/:id", handlers.GetUserByID)
	}
}

func (suite *FunctionalTestSuite) getAuthToken() string {
	loginReq := models.LoginRequest{
		Username: "user123",
		Password: "1",
	}

	body, _ := json.Marshal(loginReq)
	req := httptest.NewRequest("POST", "/api/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		suite.T().Fatalf("Failed to get auth token, status: %d, response: %s", w.Code, w.Body.String())
	}

	var resp models.LoginResponse
	err := json.Unmarshal(w.Body.Bytes(), &resp)
	if err != nil {
		return ""
	}

	return resp.Token
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

func (suite *FunctionalTestSuite) TestRegisterHandler() {
	t := suite.T()

	registerReq := models.RegisterRequest{
		Username: "newuser",
		Password: "password123",
		Email:    "new@example.com",
		FullName: "New User",
	}

	body, _ := json.Marshal(registerReq)
	req := httptest.NewRequest("POST", "/api/register", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusCreated, w.Code)

	var resp models.RegisterResponse
	err := json.Unmarshal(w.Body.Bytes(), &resp)
	if err != nil {
		return
	}
	assert.NotEmpty(t, resp.Token)

	var count int
	err = suite.db.QueryRow("SELECT COUNT(*) FROM users WHERE username = ?", "newuser").Scan(&count)
	assert.NoError(t, err)
	assert.Equal(t, 1, count)
}

func TestFunctionalTestSuite(t *testing.T) {
	if testing.Short() {
		t.Skip("Skipping functional tests in short mode")
	}
	suite.Run(t, new(FunctionalTestSuite))
}
