package ft

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/models"
	_ "modernc.org/sqlite"
	"net/http"
	"net/http/httptest"
	_ "testing"
)

func (suite *FunctionalTestSuite) TestGetCourses() {
	t := suite.T()

	req := httptest.NewRequest("GET", "/api/courses", nil)
	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var courses []models.Course
	json.Unmarshal(w.Body.Bytes(), &courses)
	assert.Len(t, courses, 3)
	assert.Equal(t, "SQL Injection", courses[0].VulnerabilityType)
}

func (suite *FunctionalTestSuite) TestGetCourseByID() {
	t := suite.T()

	req := httptest.NewRequest("GET", "/api/courses/1", nil)
	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var course models.Course
	json.Unmarshal(w.Body.Bytes(), &course)
	assert.Equal(t, 1, course.ID)
	assert.Equal(t, "SQL Injection", course.VulnerabilityType)
	assert.Len(t, course.Tasks, 2)
}

func (suite *FunctionalTestSuite) TestCompleteTask() {
	t := suite.T()

	req := httptest.NewRequest("POST", "/api/progress/2/tasks/3/complete", nil)
	req.Header.Set("Authorization", "Bearer "+suite.token)

	w := httptest.NewRecorder()
	suite.router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var count int
	err := suite.db.QueryRow("SELECT COUNT(*) FROM user_progress WHERE user_id = 2 AND task_id = 3").Scan(&count)
	assert.NoError(t, err)
	assert.Equal(t, 1, count)
}
