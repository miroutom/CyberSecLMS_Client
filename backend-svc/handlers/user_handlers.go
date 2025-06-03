package handlers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
	"io"
	"lmsmodule/backend-svc/mail"
	"lmsmodule/backend-svc/models"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"time"
)

// GetUserProfile возвращает профиль текущего пользователя
// @Summary Get user profile
// @Description Get current user's profile information with courses and progress
// @Tags Profile
// @Produce json
// @Success 200 {object} models.User
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /profile [get]
func GetUserProfile(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	user, err := Store.GetUserByID(userID.(int))
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve user profile"})
		return
	}

	user.PasswordHash = ""
	user.TOTPSecret = ""

	c.JSON(http.StatusOK, user)
}

// UpdateUserProfile обновляет профиль пользователя
// @Summary Update user profile
// @Description Update current user's profile information (email, full name)
// @Tags Profile
// @Accept json
// @Produce json
// @Param request body models.UpdateProfileRequest true "Profile update data"
// @Success 200 {object} map[string]string
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /profile [put]
func UpdateUserProfile(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	var req models.UpdateProfileRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request data"})
		return
	}

	req.Password = ""

	err := Store.UpdateUserProfile(userID.(int), req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update profile"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Profile updated successfully"})
}

// InitDeleteAccount принимает запрос на удаление и отправляет код подтверждения
// @Summary Initialize account deletion
// @Description Request account deletion (sends verification code)
// @Tags Account
// @Accept json
// @Produce json
// @Param request body models.DeleteAccountInitRequest true "Password confirmation"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /account/delete [post]
func InitDeleteAccount(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	var req models.DeleteAccountInitRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	user, err := Store.GetUserByID(userID.(int))
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get user data"})
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid password"})
		return
	}

	code := ""
	for i := 0; i < 6; i++ {
		code += strconv.Itoa(rand.Intn(10))
	}

	if err := Store.SaveOTPCode(user.ID, code); err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to save verification code"})
		return
	}

	if err := mail.SendDeleteAccountEmail(user.Email, code); err != nil {
		fmt.Printf("Error sending delete account email: %v\n", err)
	}

	c.JSON(http.StatusOK, models.SuccessResponse{
		Message: "Verification code sent to your email. Use it to confirm account deletion.",
	})
}

// ConfirmDeleteAccount подтверждает удаление аккаунта
// @Summary Confirm account deletion
// @Description Delete account permanently using verification code
// @Tags Account
// @Accept json
// @Produce json
// @Param request body models.DeleteAccountConfirmRequest true "Verification code"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /account/delete/confirm [post]
func ConfirmDeleteAccount(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	var req models.DeleteAccountConfirmRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	valid, err := Store.VerifyOTPCode(userID.(int), req.Code)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to verify code"})
		return
	}

	if !valid {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid or expired verification code"})
		return
	}

	if err := Store.ClearOTPCode(userID.(int)); err != nil {
		fmt.Printf("Error clearing OTP code: %v\n", err)
	}

	if err := Store.DeleteUser(userID.(int)); err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to delete account"})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{
		Message: "Your account has been successfully deleted.",
	})
}

// UpdateProfileImageHandler загружает и обновляет изображение профиля
// @Summary Upload profile image
// @Description Upload a new profile image for the current user
// @Tags Profile
// @Accept multipart/form-data
// @Produce json
// @Param image formData file true "Profile image (JPEG, PNG or GIF, max 2MB)"
// @Success 200 {object} map[string]string
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /account/profile/image [post]
func UpdateProfileImageHandler(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	file, header, err := c.Request.FormFile("image")
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Image file required"})
		return
	}
	defer file.Close()

	if header.Size > 2<<20 {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Image file too large (max 2MB)"})
		return
	}

	contentType := header.Header.Get("Content-Type")
	if contentType != "image/jpeg" && contentType != "image/png" && contentType != "image/gif" {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Only JPEG, PNG and GIF images are allowed"})
		return
	}

	uploadsDir := "/uploads/profiles"
	if err := os.MkdirAll(uploadsDir, 0755); err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to create uploads directory"})
		return
	}

	ext := filepath.Ext(header.Filename)
	fileName := fmt.Sprintf("user_%d_%d%s", userID.(int), time.Now().Unix(), ext)
	filePath := filepath.Join(uploadsDir, fileName)

	out, err := os.Create(filePath)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to create file"})
		return
	}
	defer out.Close()

	_, err = io.Copy(out, file)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to save file"})
		return
	}

	imageURL := fmt.Sprintf("/uploads/profiles/%s", fileName)

	err = Store.UpdateUserProfileImage(userID.(int), imageURL)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update profile image"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message":  "Profile image updated successfully",
		"imageUrl": imageURL,
	})
}

// ChangePassword меняет пароль при авторизированном запросе при соотвествии старого пароля
// @Summary Change password
// @Description Change user password (requires current password)
// @Tags Profile
// @Accept json
// @Produce json
// @Param request body models.ChangePasswordRequest true "Password change data"
// @Success 200 {object} map[string]string
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /account/change-password [post]
func ChangePassword(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	var req models.ChangePasswordRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request data"})
		return
	}

	user, err := Store.GetUserByID(userID.(int))
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve user data"})
		return
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(req.CurrentPassword)); err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Current password is incorrect"})
		return
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.NewPassword), bcrypt.DefaultCost)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to hash password"})
		return
	}

	updateReq := models.UpdateProfileRequest{
		Password: string(hashedPassword),
	}

	err = Store.UpdateUserProfile(userID.(int), updateReq)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update password"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Password changed successfully"})
}

// ForgotPassword отправляет одноразовый код для смены пароля
// @Summary Request password reset
// @Description Sends a password reset code to the user's email
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body models.ForgotPasswordRequest true "Email for password reset"
// @Success 200 {object} models.TempTokenResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /forgot-password [post]
func ForgotPassword(c *gin.Context) {
	var req models.ForgotPasswordRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	var user models.User
	var err error

	if req.Email != "" {
		users, err := Store.SearchUsers(req.Email)
		if err != nil || len(users) == 0 {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "User not found"})
			return
		}

		userFound := false
		for _, u := range users {
			if u.Email == req.Email {
				user = u
				userFound = true
				break
			}
		}

		if !userFound {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "User not found"})
			return
		}
	} else if req.Username != "" {
		user, err = Store.GetUserByUsername(req.Username)
		if err != nil {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "User not found"})
			return
		}
	} else {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Email or username is required"})
		return
	}

	code := generateResetCode(6)

	err = Store.SaveOTPCode(user.ID, code)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to save reset code"})
		return
	}

	err = mail.SendOTPEmail(user.Email, code)
	if err != nil {
		fmt.Printf("Error sending reset code email: %v\n", err)
	}

	tempToken, err := CreateTempToken(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "System error"})
		return
	}

	c.JSON(http.StatusOK, models.TempTokenResponse{
		TempToken: tempToken,
		Message:   "Password reset code sent to your email",
	})
}

// ResetPassword проверяет код и меняет пароль
// @Summary Reset password with code
// @Description Reset password using the code sent to email
// @Tags Auth
// @Accept json
// @Produce json
// @Param request body models.ResetPasswordRequest true "Reset password data"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /reset-password [post]
func ResetPassword(c *gin.Context) {
	var req models.ResetPasswordRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request"})
		return
	}

	userID, err := validateTempToken(req.TempToken)
	if err != nil {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid token"})
		return
	}

	valid, err := Store.VerifyOTPCode(userID, req.Code)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to verify code"})
		return
	}

	if !valid {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Invalid or expired code"})
		return
	}

	err = Store.ClearOTPCode(userID)
	if err != nil {
		fmt.Printf("Error clearing OTP code: %v\n", err)
	}

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.NewPassword), bcrypt.DefaultCost)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to hash password"})
		return
	}

	updateReq := models.UpdateProfileRequest{
		Password: string(hashedPassword),
	}

	err = Store.UpdateUserProfile(userID, updateReq)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update password"})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Password has been reset successfully"})
}

func generateResetCode(length int) string {
	digits := "0123456789"
	code := make([]byte, length)
	for i := 0; i < length; i++ {
		code[i] = digits[rand.Int()%len(digits)]
	}
	return string(code)
}

// GetUserByID возвращает информацию о пользователе по ID (для админов)
// @Summary Get user by ID
// @Description Get user information by ID (admin only)
// @Tags Admin
// @Produce json
// @Param id path int true "User ID"
// @Success 200 {object} models.User
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/{id} [get]
func GetUserByID(c *gin.Context) {
	userIDStr := c.Param("id")
	userID, err := strconv.Atoi(userIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	user, err := Store.GetUserByID(userID)
	if err != nil {
		if strings.Contains(err.Error(), "not found") {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "User not found"})
		} else {
			c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve user"})
		}
		return
	}

	user.PasswordHash = ""
	user.TOTPSecret = ""

	c.JSON(http.StatusOK, user)
}

// GetAllUsers возвращает список всех пользователей системы
// @Summary Get all users
// @Description Get a list of all users (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Success 200 {array} models.User
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users [get]
func GetAllUsers(c *gin.Context) {
	users, err := Store.GetAllUsers()
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get users: " + err.Error()})
		return
	}

	// Скрываем чувствительные данные
	for i := range users {
		users[i].PasswordHash = ""
		users[i].TOTPSecret = ""
	}

	c.JSON(http.StatusOK, users)
}

// GetUsersByRole получает пользователей по роли (админ/не админ)
// @Summary Get users by role
// @Description Get a list of users with a specific role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Param is_admin query bool true "Admin role flag"
// @Success 200 {array} models.User
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/by-role [get]
func GetUsersByRole(c *gin.Context) {
	isAdminStr := c.Query("is_admin")
	isAdmin := isAdminStr == "true"

	users, err := Store.GetUsersByRole(isAdmin)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get users: " + err.Error()})
		return
	}

	// Скрываем чувствительные данные
	for i := range users {
		users[i].PasswordHash = ""
		users[i].TOTPSecret = ""
	}

	c.JSON(http.StatusOK, users)
}

// SearchUsers ищет пользователей по строке запроса
// @Summary Search users
// @Description Search for users by username, email or full name (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Param query query string true "Search query"
// @Success 200 {array} models.User
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/search [get]
func SearchUsers(c *gin.Context) {
	query := c.Query("query")
	if query == "" {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Search query is required"})
		return
	}

	users, err := Store.SearchUsers(query)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to search users: " + err.Error()})
		return
	}

	// Скрываем чувствительные данные
	for i := range users {
		users[i].PasswordHash = ""
		users[i].TOTPSecret = ""
	}

	c.JSON(http.StatusOK, users)
}

// UpdateUserStatus меняет статус активности пользователя
// @Summary Update user status
// @Description Update the active status of a user (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Param id path int true "User ID"
// @Param request body models.UpdateStatusRequest true "Status data"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/{id}/status [put]
func UpdateUserStatus(c *gin.Context) {
	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	var req models.UpdateStatusRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request data: " + err.Error()})
		return
	}

	err = Store.UpdateUserStatus(targetUserID, req.IsActive)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update user status: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "User status updated successfully"})
}

// PromoteToAdmin повышает пользователя до уровня администратора
// @Summary Promote user to admin
// @Description Promote a user to admin role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Param id path int true "User ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/{id}/promote [post]
func PromoteToAdmin(c *gin.Context) {
	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	err = Store.PromoteToAdmin(targetUserID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to promote user: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "User promoted to admin successfully"})
}

// DemoteFromAdmin понижает администратора до обычного пользователя
// @Summary Demote user from admin
// @Description Demote a user from admin role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Param id path int true "User ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Security BearerAuth
// @Router /admin/users/{id}/demote [post]
func DemoteFromAdmin(c *gin.Context) {
	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	err = Store.DemoteFromAdmin(targetUserID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to demote user: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "User demoted from admin successfully"})
}
