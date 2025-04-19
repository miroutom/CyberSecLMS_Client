package handlers

import (
	"database/sql"
	"errors"
	"github.com/gin-gonic/gin"
	jwt "github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"time"
)

// LoginRequest represents login request
type LoginRequest struct {
	Username string `json:"username" binding:"required" example:"admin"`
	Password string `json:"password" binding:"required" example:"password123"`
}

// RegisterRequest represents registration request
type RegisterRequest struct {
	Username string `json:"username" binding:"required" example:"newuser"`
	Password string `json:"password" binding:"required" example:"newpassword123"`
	Email    string `json:"email" binding:"required" example:"user@example.com"`
	FullName string `json:"fullName" binding:"required" example:"New User"`
}

// LoginResponse represents successful login response
type LoginResponse struct {
	Token string `json:"token" example:"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."`
	User  struct {
		ID       int    `json:"id" example:"1"`
		Username string `json:"username" example:"admin"`
		FullName string `json:"fullName" example:"Admin User"`
		Email    string `json:"email" example:"admin@example.com"`
	} `json:"user"`
}

// ErrorResponse represents error response
type ErrorResponse struct {
	Error string `json:"error" example:"Invalid credentials"`
}

// RegisterHandler handles user registration
// @Summary Register new user
// @Description Create a new user account
// @Tags Authentication
// @Accept json
// @Produce json
// @Param request body RegisterRequest true "Registration data"
// @Success 201 {object} LoginResponse "User created"
// @Failure 400 {object} ErrorResponse "Invalid request"
// @Failure 409 {object} ErrorResponse "User already exists"
// @Failure 500 {object} ErrorResponse "Server error"
// @Router /register [post]
func RegisterHandler(c *gin.Context) {
	var req RegisterRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, ErrorResponse{"Invalid request"})
		return
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		c.JSON(http.StatusInternalServerError, ErrorResponse{"Password hashing failed"})
		return
	}

	var userID int
	err = db.QueryRow(`
		INSERT INTO users (username, password_hash, email, full_name) 
		VALUES (?, ?, ?, ?) RETURNING id`,
		req.Username, hashedPassword, req.Email, req.FullName).Scan(&userID)

	if err != nil {
		if isDuplicateKeyError(err) {
			c.JSON(http.StatusConflict, ErrorResponse{"User already exists"})
		} else {
			c.JSON(http.StatusInternalServerError, ErrorResponse{"Database error"})
		}
		return
	}

	generateAndSendToken(c, userID, req.Username, req.FullName, req.Email)
}

// LoginHandler handles user login
// @Summary Authenticate user
// @Description Verify user credentials and return JWT token
// @Tags Authentication
// @Accept json
// @Produce json
// @Param request body LoginRequest true "Login data"
// @Success 200 {object} LoginResponse "Authentication successful"
// @Failure 400 {object} ErrorResponse "Invalid request"
// @Failure 401 {object} ErrorResponse "Invalid credentials"
// @Failure 500 {object} ErrorResponse "Server error"
// @Router /login [post]
func LoginHandler(c *gin.Context) {
	var req LoginRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, ErrorResponse{"Invalid request"})
		return
	}

	var user struct {
		ID           int
		Username     string
		PasswordHash string
		FullName     string
		Email        string
	}

	err := db.QueryRow(`
		SELECT id, username, password_hash, full_name, email 
		FROM users 
		WHERE username = ?`, req.Username).Scan(
		&user.ID, &user.Username, &user.PasswordHash, &user.FullName, &user.Email)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			c.JSON(http.StatusUnauthorized, ErrorResponse{"Invalid credentials"})
		} else {
			c.JSON(http.StatusInternalServerError, ErrorResponse{"Database error"})
		}
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, ErrorResponse{"Invalid credentials"})
		return
	}

	_, _ = db.Exec("UPDATE users SET last_login = NOW() WHERE id = ?", user.ID)
	generateAndSendToken(c, user.ID, user.Username, user.FullName, user.Email)
}

func generateAndSendToken(c *gin.Context, userID int, username, fullName, email string) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub":      userID,
		"username": username,
		"exp":      time.Now().Add(time.Hour * 24).Unix(),
		"iat":      time.Now().Unix(),
	})

	tokenString, err := token.SignedString([]byte("your-secret-key"))
	if err != nil {
		c.JSON(http.StatusInternalServerError, ErrorResponse{"Failed to generate token"})
		return
	}

	c.JSON(http.StatusOK, LoginResponse{
		Token: tokenString,
		User: struct {
			ID       int    `json:"id" example:"1"`
			Username string `json:"username" example:"admin"`
			FullName string `json:"fullName" example:"Admin User"`
			Email    string `json:"email" example:"admin@example.com"`
		}{
			ID:       userID,
			Username: username,
			FullName: fullName,
			Email:    email,
		},
	})
}

func isDuplicateKeyError(err error) bool {
	// Implement database-specific duplicate key error check
	return err != nil && errors.Is(err, sql.ErrNoRows)
}
