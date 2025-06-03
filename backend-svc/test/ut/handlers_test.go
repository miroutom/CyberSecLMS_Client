package ut

import (
	"database/sql"
	"errors"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
)

type MockStore struct {
	mock.Mock
}

func (m *MockStore) IsAdmin(userID int) (bool, error) {
	args := m.Called(userID)
	return args.Bool(0), args.Error(1)
}

func (m *MockStore) GetCourses() ([]models.Course, error) {
	return nil, nil
}

func (m *MockStore) GetCourseByID(id int) (models.Course, error) {
	return models.Course{}, nil
}

func (m *MockStore) GetUserProgress(userID int) (models.UserProgress, error) {
	return models.UserProgress{}, nil
}

func (m *MockStore) CompleteTask(userID, taskID int) error {
	return nil
}

func (m *MockStore) CreateUser(user models.User) error {
	return nil
}

func (m *MockStore) GetUserByUsername(username string) (models.User, error) {
	return models.User{}, nil
}

func (m *MockStore) GetUserByID(id int) (models.User, error) {
	return models.User{}, nil
}

func (m *MockStore) UpdateUserLastLogin(userID int) error {
	return nil
}

func (m *MockStore) UpdateUserProfile(userID int, data models.UpdateProfileRequest) error {
	return nil
}

func (m *MockStore) Enable2FA(userID int) error {
	return nil
}

func (m *MockStore) UpdateUserProfileImage(userID int, imageURL string) error {
	return nil
}

func (m *MockStore) DeleteUser(userID int) error {
	return nil
}

func (m *MockStore) GetAllUsers() ([]models.User, error) {
	return nil, nil
}

func (m *MockStore) GetUsersByRole(isAdmin bool) ([]models.User, error) {
	return nil, nil
}

func (m *MockStore) SearchUsers(query string) ([]models.User, error) {
	return nil, nil
}

func (m *MockStore) UpdateUserStatus(userID int, isActive bool) error {
	return nil
}

func (m *MockStore) PromoteToAdmin(userID int) error {
	return nil
}

func (m *MockStore) DemoteFromAdmin(userID int) error {
	return nil
}

func (m *MockStore) SaveOTPCode(userID int, code string) error {
	return nil
}

func (m *MockStore) VerifyOTPCode(userID int, code string) (bool, error) {
	return false, nil
}

func (m *MockStore) ClearOTPCode(userID int) error {
	return nil
}

func TestUseStorage(t *testing.T) {
	mockStore := new(storage.MockStorage)
	handlers.UseStorage(mockStore)

	assert.Equal(t, mockStore, handlers.Store)
}

func TestCheckAdminRights(t *testing.T) {
	mockStore := new(MockStore)
	handlers.Store = mockStore

	t.Run("User is admin", func(t *testing.T) {
		mockStore.On("IsAdmin", 1).Return(true, nil).Once()

		isAdmin, err := handlers.CheckAdminRights(1)

		assert.NoError(t, err)
		assert.True(t, isAdmin)
		mockStore.AssertExpectations(t)
	})

	t.Run("User is not admin", func(t *testing.T) {
		mockStore.On("IsAdmin", 2).Return(false, nil).Once()

		isAdmin, err := handlers.CheckAdminRights(2)

		assert.NoError(t, err)
		assert.False(t, isAdmin)
		mockStore.AssertExpectations(t)
	})

	t.Run("Error checking admin rights", func(t *testing.T) {
		expectedError := errors.New("database error")
		mockStore.On("IsAdmin", 3).Return(false, expectedError).Once()

		isAdmin, err := handlers.CheckAdminRights(3)

		assert.Error(t, err)
		assert.Equal(t, expectedError, err)
		assert.False(t, isAdmin)
		mockStore.AssertExpectations(t)
	})
}

func TestJWTSecret(t *testing.T) {
	originalSecret := handlers.JWTSecret
	defer func() {
		handlers.JWTSecret = originalSecret
	}()

	handlers.JWTSecret = "new_test_secret"
	assert.Equal(t, "new_test_secret", handlers.JWTSecret)
}

func TestDbVariable(t *testing.T) {
	assert.Nil(t, handlers.Db)

	db := &sql.DB{}
	handlers.Db = db

	assert.Equal(t, db, handlers.Db)
}
