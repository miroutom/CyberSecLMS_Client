package handlers

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	jwt "github.com/golang-jwt/jwt/v5"
	"github.com/pquerna/otp/totp"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend/models"
	"net/http"
	"time"
)

const (
	jwtSecret     = "your_strong_secret_here"
	tempJwtSecret = "temp_2fa_secret_here"
)

// @Summary Register new user
// @Tags Authentication
// @Accept json
// @Produce json
// @Param request body RegisterRequest true "Registration data"
// @Success 201 {object} models.SuccessResponse "User created"
// @Failure 400 {object} models.ErrorResponse "Invalid request"
// @Failure 409 {object} models.ErrorResponse "User already exists"
// @Failure 500 {object} models.ErrorResponse "Server error"
// @Router /register [post]
func RegisterHandler(c *gin.Context) {
	var req models.RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request data"})
		return
	}

	checkStmt, err := Db.Prepare(`
        SELECT EXISTS(
            SELECT 1 FROM users 
            WHERE username = ? OR email = ?
        )`)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer checkStmt.Close()

	var exists bool
	err = checkStmt.QueryRow(req.Username, req.Email).Scan(&exists)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database check failed"})
		return
	}

	if exists {
		c.JSON(http.StatusConflict, models.ErrorResponse{Error: "Username or email already exists"})
		return
	}

	hashedPassword, err := bcrypt.GenerateFromPassword(
		[]byte(req.Password),
		bcrypt.DefaultCost,
	)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Password hashing failed"})
		return
	}

	key, err := totp.Generate(totp.GenerateOpts{
		Issuer:      "LMS System",
		AccountName: req.Username,
		SecretSize:  20,
	})
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to generate 2FA key"})
		return
	}

	insertStmt, err := Db.Prepare(`
        INSERT INTO users (
            username, 
            password_hash, 
            email, 
            full_name,
            is_active,
            totp_secret
        ) VALUES (?, ?, ?, ?, ?, ?)`)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer insertStmt.Close()

	_, err = insertStmt.Exec(
		req.Username,
		string(hashedPassword),
		req.Email,
		req.FullName,
		true,         // is_active
		key.Secret(), // totp_secret
	)

	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User registration failed"})
		return
	}

	c.JSON(http.StatusCreated, models.SuccessResponse{Message: "User created successfully"})
}

// @Summary Login
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body LoginRequest true "Credentials"
// @Success 200 {object} models.TempTokenResponse "OTP sent to registered email (if 2FA enabled)"
// @Success 200 {object} models.LoginResponse "User logged in successfully (if 2FA disabled)"
// @Failure 400 {object} models.ErrorResponse "Invalid request"
// @Failure 401 {object} models.ErrorResponse "Invalid credentials"
// @Failure 500 {object} models.ErrorResponse "System error"
// @Router /login [post]
func LoginHandler(c *gin.Context) {
	var req models.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	stmt, err := Db.Prepare("SELECT id, username, password_hash, email, full_name, totp_secret, is_2fa_enabled FROM users WHERE username = ?")
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer stmt.Close()

	var user models.User
	err = stmt.QueryRow(req.Username).Scan(&user.ID, &user.Username, &user.PasswordHash, &user.Email, &user.FullName, &user.TOTPSecret, &user.Is2FAEnabled)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid credentials"})
		} else {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		}
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid credentials"})
		return
	}

	updateStmt, err := Db.Prepare("UPDATE users SET last_login = NOW() WHERE id = ?")
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}
	defer updateStmt.Close()

	_, err = updateStmt.Exec(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	// Проверяем, включена ли двухфакторная аутентификация
	if user.Is2FAEnabled {
		// Если 2FA включена, отправляем OTP и временный токен
		code, _ := totp.GenerateCode(user.TOTPSecret, time.Now())
		sendOTPEmail(user.Email, code)

		tempToken, err := createTempToken(user.ID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
			return
		}

		c.JSON(http.StatusOK, models.TempTokenResponse{
			TempToken: tempToken,
			Message:   "OTP sent to registered email",
		})
	} else {
		// Если 2FA не включена, выдаём обычный JWT-токен
		token, err := createJWTToken(user.ID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
			return
		}

		c.JSON(http.StatusOK, models.LoginResponse{
			Token:    token,
			UserID:   user.ID,
			Username: user.Username,
			Email:    user.Email,
		})
	}
}

// @Summary Verify OTP
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body VerifyOTPRequest true "OTP data"
// @Success 200 {object} models.LoginResponse "User logged in successfully"
// @Failure 400 {object} models.ErrorResponse "Invalid request"
// @Failure 401 {object} models.ErrorResponse "Invalid token or OTP"
// @Failure 500 {object} models.ErrorResponse "System error"
// @Router /verify-otp [post]
func VerifyOTPHandler(c *gin.Context) {
	var req models.VerifyOTPRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	userID, err := validateTempToken(req.TempToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token"})
		return
	}

	stmt, err := Db.Prepare("SELECT id, username, email, totp_secret FROM users WHERE id = ?")
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer stmt.Close()

	var user models.User
	err = stmt.QueryRow(userID).Scan(&user.ID, &user.Username, &user.Email, &user.TOTPSecret)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User not found"})
		return
	}

	if !totp.Validate(req.OTP, user.TOTPSecret) {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid OTP code"})
		return
	}

	token, err := createJWTToken(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	c.JSON(http.StatusOK, models.LoginResponse{
		Token:    token,
		UserID:   user.ID,
		Username: user.Username,
		Email:    user.Email,
	})
}

// Enable2FAHandler включает двухфакторную аутентификацию для пользователя
// @Summary Enable 2FA
// @Description Включает двухфакторную аутентификацию для пользователя после проверки OTP кода
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body Enable2FARequest true "OTP данные для верификации"
// @Success 200 {object} models.Enable2FAResponse "2FA успешно включена"
// @Failure 400 {object} models.ErrorResponse "Неверный запрос или OTP код"
// @Failure 401 {object} models.ErrorResponse "Неавторизованный доступ"
// @Failure 500 {object} models.ErrorResponse "Ошибка сервера"
// @Router /account/2fa/enable [post]
func Enable2FAHandler(c *gin.Context) {
	userID := c.GetString("userID")
	var req models.Enable2FARequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	stmt, err := Db.Prepare("SELECT totp_secret FROM users WHERE id = ?")
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer stmt.Close()

	var secret string
	err = stmt.QueryRow(userID).Scan(&secret)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}

	if !totp.Validate(req.OTP, secret) {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid OTP code"})
		return
	}

	updateStmt, err := Db.Prepare("UPDATE users SET is_2fa_enabled = TRUE WHERE id = ?")
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Database error"})
		return
	}
	defer updateStmt.Close()

	_, err = updateStmt.Exec(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to enable 2FA"})
		return
	}

	c.JSON(http.StatusOK, models.Enable2FAResponse{Status: "2FA enabled"})
}

func createTempToken(userID int) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub": userID,
		"exp": time.Now().Add(5 * time.Minute).Unix(),
	})
	return token.SignedString([]byte(tempJwtSecret))
}

func validateTempToken(tokenString string) (int, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(tempJwtSecret), nil
	})
	if err != nil || !token.Valid {
		return 0, errors.New("invalid token")
	}
	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return 0, errors.New("invalid token claims")
	}
	userID, ok := claims["sub"].(float64)
	if !ok {
		return 0, errors.New("invalid user id in token")
	}
	return int(userID), nil
}

func createJWTToken(userID int) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub": userID,
		"exp": time.Now().Add(24 * time.Hour).Unix(),
	})
	return token.SignedString([]byte(jwtSecret))
}

func sendOTPEmail(email, code string) {
	// Implement real email sending logic
	fmt.Printf("OTP for %s: %s\n", email, code) // mock
}
