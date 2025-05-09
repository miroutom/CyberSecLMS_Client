package handlers

import (
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	jwt "github.com/golang-jwt/jwt/v5"
	"github.com/pquerna/otp/totp"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend-svc/mail"
	"lmsmodule/backend-svc/models"
	"net/http"
	"time"
)

const (
	tempJWTSecret = "temp_2fa_secret_here"
)

// @Summary Register new user
// @Tags Authentication
// @Accept json
// @Produce json
// @Param request body models.RegisterRequest true "Registration data"
// @Success 201 {object} models.RegisterResponse "User created"
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

	user := models.User{
		Username:     req.Username,
		PasswordHash: string(hashedPassword),
		Email:        req.Email,
		FullName:     req.FullName,
		TOTPSecret:   key.Secret(),
		Is2FAEnabled: true,
		IsActive:     true,
	}

	err = Store.CreateUser(user)
	if err != nil {
		if err.Error() == "username or email already exists" {
			c.JSON(http.StatusConflict, models.ErrorResponse{Error: "Username or email already exists"})
		} else {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User registration failed: " + err.Error()})
		}
		return
	}

	createdUser, err := Store.GetUserByUsername(user.Username)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User created but failed to retrieve"})
		return
	}

	token, err := createJWTToken(createdUser.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	c.JSON(http.StatusCreated, models.RegisterResponse{
		Token:   token,
		Message: "User created successfully",
	})
}

// @Summary Login
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body models.LoginRequest true "Credentials"
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

	user, err := Store.GetUserByUsername(req.Username)
	if err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid credentials"})
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid credentials"})
		return
	}

	err = Store.UpdateUserLastLogin(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	if user.Is2FAEnabled {
		code, err := totp.GenerateCode(user.TOTPSecret, time.Now())
		if err != nil {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to generate OTP"})
			return
		}

		err = Store.SaveOTPCode(user.ID, code)
		if err != nil {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to save OTP code"})
			return
		}

		err = mail.SendOTPEmail(user.Email, code)
		if err != nil {
			fmt.Printf("Error sending OTP email: %v\n", err)
		}

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
// @Param request body models.VerifyOTPRequest true "OTP data"
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

	valid, err := Store.VerifyOTPCode(userID, req.OTP)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	if !valid {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid or expired OTP code"})
		return
	}

	err = Store.ClearOTPCode(userID)
	if err != nil {
		fmt.Printf("Error clearing OTP code: %v\n", err)
	}

	user, err := Store.GetUserByID(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User not found"})
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

// @Summary Enable 2FA
// @Description Включает двухфакторную аутентификацию для пользователя после проверки OTP кода
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body models.Enable2FARequest true "OTP данные для верификации"
// @Success 200 {object} models.Enable2FAResponse "2FA успешно включена"
// @Failure 400 {object} models.ErrorResponse "Неверный запрос или OTP код"
// @Failure 401 {object} models.ErrorResponse "Неавторизованный доступ"
// @Failure 500 {object} models.ErrorResponse "Ошибка сервера"
// @Router /account/2fa/enable [post]
func Enable2FAHandler(c *gin.Context) {
	userIDInterface, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	userID, ok := userIDInterface.(int)
	if !ok {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Invalid user ID format"})
		return
	}

	var req models.Enable2FARequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	user, err := Store.GetUserByID(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "User not found"})
		return
	}

	if !totp.Validate(req.OTP, user.TOTPSecret) {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid OTP code"})
		return
	}

	err = Store.Enable2FA(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to enable 2FA: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.Enable2FAResponse{Status: "2FA enabled"})
}

// @Summary Reload email templates
// @Tags Admin
// @Accept json
// @Produce json
// @Success 200 {object} models.SuccessResponse "Templates reloaded"
// @Failure 403 {object} models.ErrorResponse "Access denied"
// @Failure 500 {object} models.ErrorResponse "Server error"
// @Router /admin/reload-templates [post]
func ReloadTemplatesHandler(c *gin.Context) {
	if err := mail.ReloadTemplates(); err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: fmt.Sprintf("Failed to reload templates: %v", err)})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Templates reloaded successfully"})
}

func createTempToken(userID int) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub": userID,
		"exp": time.Now().Add(5 * time.Minute).Unix(),
	})
	return token.SignedString([]byte(tempJWTSecret))
}

func validateTempToken(tokenString string) (int, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(tempJWTSecret), nil
	})
	if err != nil || !token.Valid {
		return 0, errors.New("invalid token")
	}
	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return 0, errors.New("invalid token claims")
	}
	userIDFloat, ok := claims["sub"].(float64)
	if !ok {
		return 0, errors.New("invalid user id in token")
	}
	return int(userIDFloat), nil
}

func createJWTToken(userID int) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"sub": userID,
		"exp": time.Now().Add(24 * time.Hour).Unix(),
	})
	return token.SignedString([]byte(JWTSecret))
}
