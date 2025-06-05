package ft

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/models"
	"net/http"
	"net/http/httptest"
)

func (suite *FunctionalTestSuite) TestGetUserProgress() {
	t := suite.T()

	req := httptest.NewRequest("GET", "/api/progress/2", nil)
	req.Header.Set("Authorization", "Bearer "+suite.token)

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var progress models.UserProgress
	err := json.Unmarshal(w.Body.Bytes(), &progress)
	if err != nil {
		return
	}
	assert.Equal(t, 2, progress.UserID)
	assert.True(t, progress.Completed[1])
	assert.True(t, progress.Completed[2])
}
