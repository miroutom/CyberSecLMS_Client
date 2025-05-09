package handlers

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/backend-svc/models"
	"net/http"
	"strconv"
)

// @Summary Get user profile
// @Description Get the profile information of the current user
// @Tags User
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} models.UserProfile
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /profile [get]
func GetUserProfile(c *gin.Context) {
	userIDInterface, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	userID := userIDInterface.(int)

	user, err := Store.GetUserByID(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get user: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.UserProfile{
		ID:           user.ID,
		Username:     user.Username,
		Email:        user.Email,
		FullName:     user.FullName,
		Is2FAEnabled: user.Is2FAEnabled,
	})
}

// @Summary Get another user's profile
// @Description Get the profile information of another user (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path int true "User ID"
// @Success 200 {object} models.UserProfile
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /admin/users/{id} [get]
func GetUserByID(c *gin.Context) {
	userID, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	isAdmin, err := Store.IsAdmin(userID.(int))
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Error checking admin rights: " + err.Error()})
		return
	}

	if !isAdmin {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Admin access required"})
		return
	}

	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	user, err := Store.GetUserByID(targetUserID)
	if err != nil {
		c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "User not found"})
		return
	}

	c.JSON(http.StatusOK, models.UserProfile{
		ID:           user.ID,
		Username:     user.Username,
		Email:        user.Email,
		FullName:     user.FullName,
		Is2FAEnabled: user.Is2FAEnabled,
		IsAdmin:      user.IsAdmin,
		IsActive:     user.IsActive,
		LastLogin:    user.LastLogin,
	})
}

// @Summary Get all users
// @Description Get a list of all users (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {array} models.UserProfile
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /admin/users [get]
func GetAllUsers(c *gin.Context) {
	users, err := Store.GetAllUsers()
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get users: " + err.Error()})
		return
	}

	var userProfiles []models.UserProfile
	for _, user := range users {
		userProfiles = append(userProfiles, models.UserProfile{
			ID:           user.ID,
			Username:     user.Username,
			Email:        user.Email,
			FullName:     user.FullName,
			Is2FAEnabled: user.Is2FAEnabled,
			IsAdmin:      user.IsAdmin,
			IsActive:     user.IsActive,
			LastLogin:    user.LastLogin,
		})
	}

	c.JSON(http.StatusOK, userProfiles)
}

// @Summary Update user profile
// @Description Update the profile of the current user
// @Tags User
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param request body models.UpdateProfileRequest true "Profile data to update"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /profile [put]
func UpdateUserProfile(c *gin.Context) {
	userIDInterface, exists := c.Get("userID")
	if !exists {
		c.JSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
		return
	}

	userID := userIDInterface.(int)

	var req models.UpdateProfileRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request data: " + err.Error()})
		return
	}

	err := Store.UpdateUserProfile(userID, req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to update profile: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Profile updated successfully"})
}

// @Summary Get users by role
// @Description Get a list of users with a specific role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param is_admin query bool true "Admin role flag"
// @Success 200 {array} models.UserProfile
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /admin/users/by-role [get]
func GetUsersByRole(c *gin.Context) {
	isAdminStr := c.Query("is_admin")
	isAdmin := isAdminStr == "true"

	users, err := Store.GetUsersByRole(isAdmin)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to get users: " + err.Error()})
		return
	}

	var userProfiles []models.UserProfile
	for _, user := range users {
		userProfiles = append(userProfiles, models.UserProfile{
			ID:           user.ID,
			Username:     user.Username,
			Email:        user.Email,
			FullName:     user.FullName,
			Is2FAEnabled: user.Is2FAEnabled,
			IsAdmin:      user.IsAdmin,
			IsActive:     user.IsActive,
			LastLogin:    user.LastLogin,
		})
	}

	c.JSON(http.StatusOK, userProfiles)
}

// @Summary Search users
// @Description Search for users by username, email or full name (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param query query string true "Search query"
// @Success 200 {array} models.UserProfile
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
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

	var userProfiles []models.UserProfile
	for _, user := range users {
		userProfiles = append(userProfiles, models.UserProfile{
			ID:           user.ID,
			Username:     user.Username,
			Email:        user.Email,
			FullName:     user.FullName,
			Is2FAEnabled: user.Is2FAEnabled,
			IsAdmin:      user.IsAdmin,
			IsActive:     user.IsActive,
			LastLogin:    user.LastLogin,
		})
	}

	c.JSON(http.StatusOK, userProfiles)
}

// @Summary Update user status
// @Description Update the active status of a user (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path int true "User ID"
// @Param request body models.UpdateStatusRequest true "Status data"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
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

// @Summary Promote user to admin
// @Description Promote a user to admin role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path int true "User ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
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

// @Summary Demote user from admin
// @Description Demote a user from admin role (admin only)
// @Tags Admin
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param id path int true "User ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
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
