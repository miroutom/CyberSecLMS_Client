package ft

import (
	"github.com/stretchr/testify/assert"
	"lmsmodule/backend-svc/models"
	_ "modernc.org/sqlite"
	"net/http"
	_ "testing"
)

func (suite *FunctionalTestSuite) TestGetCourses() {
	t := suite.T()

	resp, err := suite.client.R().
		SetResult(&[]models.Course{}).
		Get("/api/courses")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	courses := resp.Result().(*[]models.Course)
	assert.Len(t, *courses, 3)
}

func (suite *FunctionalTestSuite) TestGetCourseByID() {
	t := suite.T()

	resp, err := suite.client.R().
		SetResult(&models.Course{}).
		Get("/api/courses/1")

	assert.NoError(t, err)
	assert.Equal(t, http.StatusOK, resp.StatusCode())

	course := resp.Result().(*models.Course)
	assert.Equal(t, 1, course.ID)
	assert.Equal(t, "SQL Injection", course.VulnerabilityType)
	assert.NotEmpty(t, course.Tasks)
}
