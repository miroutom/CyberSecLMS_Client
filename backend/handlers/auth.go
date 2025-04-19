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

// LoginRequest представляет структуру запроса для аутентификации
// @Description Запрос на авторизацию пользователя
type LoginRequest struct {
	// Username
	Username string `json:"username" binding:"required" example:"admin"`
	// Password
	Password string `json:"password" binding:"required" example:"password123"`
}

// LoginResponse представляет структуру успешного ответа
// @Description Ответ с JWT токеном и информацией о пользователе
type LoginResponse struct {
	// JWT токен для аутентификации
	Token string `json:"token" example:"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."`
	// Информация о пользователе
	User struct {
		// ID пользователя
		ID int `json:"id" example:"1"`
		// Имя пользователя
		Username string `json:"username" example:"admin"`
		// Полное имя
		FullName string `json:"fullName" example:"Администратор Системы"`
		// Email
		Email string `json:"email" example:"admin@lms.example"`
	} `json:"user"`
}

// ErrorResponse представляет структуру ошибки
// @Description Стандартный формат ошибки
type ErrorResponse struct {
	// Сообщение об ошибке
	Error string `json:"error" example:"Invalid credentials"`
}

// LoginHandler обрабатывает запрос на авторизацию
// @Summary Аутентификация пользователя
// @Description Проверяет учетные данные пользователя и возвращает JWT токен
// @Tags Аутентификация
// @Accept json
// @Produce json
// @Param request body LoginRequest true "Данные для входа"
// @Success 200 {object} LoginResponse "Успешная аутентификация"
// @Failure 400 {object} ErrorResponse "Неверный формат запроса"
// @Failure 401 {object} ErrorResponse "Неверные учетные данные"
// @Failure 500 {object} ErrorResponse "Ошибка сервера"
// @Router /login [post]
func LoginHandler(c *gin.Context) {
	var req LoginRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, ErrorResponse{Error: "Invalid request"})
		return
	}

	// Получаем пользователя из базы данных
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
			c.JSON(http.StatusUnauthorized, ErrorResponse{Error: "Invalid credentials"})
		} else {
			c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Database error"})
		}
		return
	}

	// Проверяем пароль
	err = bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password))
	if err != nil {
		c.JSON(http.StatusUnauthorized, ErrorResponse{Error: "Invalid credentials"})
		return
	}

	// Генерируем JWT токен
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub":      user.ID,
		"username": user.Username,
		"exp":      time.Now().Add(time.Hour * 24).Unix(),
		"iat":      time.Now().Unix(),
	})

	secret := "your-secret-key" // В реальном приложении брать из конфига
	tokenString, err := token.SignedString([]byte(secret))
	if err != nil {
		c.JSON(http.StatusInternalServerError, ErrorResponse{Error: "Failed to generate token"})
		return
	}

	// Обновляем время последнего входа
	_, _ = db.Exec("UPDATE users SET last_login = NOW() WHERE id = ?", user.ID)

	// Формируем ответ
	response := LoginResponse{
		Token: tokenString,
		User: struct {
			ID       int    `json:"id" example:"1"`
			Username string `json:"username" example:"admin"`
			FullName string `json:"fullName" example:"Администратор Системы"`
			Email    string `json:"email" example:"admin@lms.example"`
		}(struct {
			ID       int    `json:"id"`
			Username string `json:"username"`
			FullName string `json:"fullName"`
			Email    string `json:"email"`
		}{
			ID:       user.ID,
			Username: user.Username,
			FullName: user.FullName,
			Email:    user.Email,
		}),
	}

	c.JSON(http.StatusOK, response)
}
