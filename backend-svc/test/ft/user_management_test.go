package ft

import (
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/models"
	"net/http"
)

func (suite *FunctionalTestSuite) TestGetUserProfile() {
	t := suite.T()

	resp, err := suite.client.R().
		SetAuthToken(suite.token).
		SetResult(&models.User{}).
		Get("/api/profile")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	user := resp.Result().(*models.User)
	assert.Equal(t, "user123", user.Username)
	assert.Equal(t, "user@example.com", user.Email)
}

func (suite *FunctionalTestSuite) TestUpdateUserProfile() {
	t := suite.T()

	updateReq := models.UpdateProfileRequest{
		FullName: "Updated User Name",
	}

	resp, err := suite.client.R().
		SetAuthToken(suite.token).
		SetBody(updateReq).
		Put("/api/profile")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	var fullName string
	err = suite.db.QueryRow("SELECT full_name FROM users WHERE username = ?", "user123").Scan(&fullName)
	assert.NoError(t, err)
	assert.Equal(t, "Updated User Name", fullName)
}

func (suite *FunctionalTestSuite) TestGetUserSubmissions() {
	t := suite.T()

	resp, err := suite.client.R().
		SetAuthToken(suite.token).
		SetResult(&[]models.TaskSubmissionDetails{}).
		Get("/api/progress/2/submissions")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	submissions := resp.Result().(*[]models.TaskSubmissionDetails)
	assert.GreaterOrEqual(t, len(*submissions), 2)
}

func (suite *FunctionalTestSuite) TestGetUserLearningPath() {
	t := suite.T()

	resp, err := suite.client.R().
		SetAuthToken(suite.token).
		SetResult(&models.LearningPath{}).
		Get("/api/progress/2/learning-path")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	learningPath := resp.Result().(*models.LearningPath)
	assert.Equal(t, 2, learningPath.UserID)
	assert.NotEmpty(t, learningPath.Recommendations)
}
