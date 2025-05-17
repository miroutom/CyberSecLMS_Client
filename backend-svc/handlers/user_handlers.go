package handlers

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"io"
	"lmsmodule/backend-svc/models"
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
// @Description Update current user's profile information
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

	err := Store.UpdateUserProfile(userID.(int), req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update profile"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Profile updated successfully"})
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

	uploadsDir := "./uploads/profiles"
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
