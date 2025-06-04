package ft

import (
	"bytes"
	"encoding/json"
	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"net/http"
	"net/http/httptest"
)

func (suite *FunctionalTestSuite) TestGetUserProfile() {
	t := suite.T()

	req := httptest.NewRequest("GET", "/api/profile", nil)
	req.Header.Set("Authorization", "Bearer "+suite.token)

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var user models.User
	err := json.Unmarshal(w.Body.Bytes(), &user)
	if err != nil {
		return
	}
	assert.Equal(t, "user123", user.Username)
	assert.Equal(t, "user@example.com", user.Email)
	assert.Empty(t, user.PasswordHash)
}

func (suite *FunctionalTestSuite) TestUpdateUserProfile() {
	t := suite.T()

	updateReq := models.UpdateProfileRequest{
		FullName: "Updated User Name",
	}

	body, _ := json.Marshal(updateReq)
	req := httptest.NewRequest("PUT", "/api/profile", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+suite.token)

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var fullName string
	err := suite.db.QueryRow("SELECT full_name FROM users WHERE id = 2").Scan(&fullName)
	assert.NoError(t, err)
	assert.Equal(t, "Updated User Name", fullName)
}

func AdminAuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		userID, exists := c.Get("userID")
		if !exists {
			c.AbortWithStatusJSON(http.StatusUnauthorized, models.ErrorResponse{Error: "Unauthorized"})
			return
		}

		isAdmin, err := handlers.CheckAdminRights(userID.(int))
		if err != nil {
			c.AbortWithStatusJSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Error checking admin rights: " + err.Error()})
			return
		}

		if !isAdmin {
			c.AbortWithStatusJSON(http.StatusForbidden, models.ErrorResponse{Error: "Admin access required"})
			return
		}

		c.Next()
	}
}

func (suite *FunctionalTestSuite) TestAdminAccess() {
	t := suite.T()

	adminLoginReq := models.LoginRequest{
		Username: "admin",
		Password: "admin123",
	}

	body, _ := json.Marshal(adminLoginReq)
	req := httptest.NewRequest("POST", "/api/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var loginResp models.LoginResponse
	err := json.Unmarshal(w.Body.Bytes(), &loginResp)
	if err != nil {
		return
	}
	adminToken := loginResp.Token

	req = httptest.NewRequest("GET", "/api/admin/users", nil)
	req.Header.Set("Authorization", "Bearer "+adminToken)

	w = httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var users []models.User
	err = json.Unmarshal(w.Body.Bytes(), &users)
	if err != nil {
		return
	}
	assert.GreaterOrEqual(t, len(users), 2)

	req = httptest.NewRequest("GET", "/api/admin/users", nil)
	req.Header.Set("Authorization", "Bearer "+suite.token)

	w = httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusForbidden, w.Code)
}
