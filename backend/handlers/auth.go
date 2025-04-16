package handlers

import (

	"github.com/golang-jwt/jwt/v5"
	"github.com/gin-gonic/gin"
	"lmsmodule/backend/storage"
	"net/http"
	"time"
)

// LoginRequest represents the request body for login.
// @Title Login request
// @Description The structure for login request containing username and password.
type LoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

// LoginHandler handles the login request.
// @Summary Login handler
// @Description Handles the login request and returns a JWT token if the credentials are valid.
// @ID login
// @Produce json
// @Param body body handlers.LoginRequest true "Login request body"
// @Success 200 {object} map[string]interface{} "Successful login"
// @Failure 400 {object} map[string]interface{} "Invalid request"
// @Failure 401 {object} map[string]interface{} "Invalid credentials"
// @Router /login [post]
func LoginHandler(c *gin.Context) {
	var req LoginRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request"})
		return
	}

	user, exists := storage.Users[req.Username]
	if !exists || user.Password != req.Password {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid credentials"})
		return
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"username": req.Username,
		"exp":      time.Now().Add(time.Hour * 24).Unix(), // токен действителен в течение 24 часов
	})

	secret := "your-secret-key" // заменить на нормальный секретный ключ
	tokenString, err := token.SignedString([]byte(secret))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate token"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"token": tokenString})
}
