package ft

import (
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/models"
	"net/http"
)

func (suite *FunctionalTestSuite) TestGetUserProgress() {
	t := suite.T()

	resp, err := suite.client.R().
		SetAuthToken(suite.token).
		SetResult(&models.UserProgress{}).
		Get("/api/progress/2")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	progress := resp.Result().(*models.UserProgress)
	assert.Equal(t, 2, progress.UserID)
	assert.True(t, progress.Completed[1])
	assert.True(t, progress.Completed[2])
}
