package ut

import (
	"lmsmodule/backend-svc/models"
	"lmsmodule/backend-svc/storage"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestMockStorage_Courses(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("GetCourses", func(t *testing.T) {
		courses, err := mockStore.GetCourses()

		assert.NoError(t, err)
		assert.Equal(t, 3, len(courses))
		assert.Equal(t, "SQL Injection", courses[0].VulnerabilityType)
		assert.Equal(t, "XSS", courses[1].VulnerabilityType)
		assert.Equal(t, "CSRF", courses[2].VulnerabilityType)

		assert.Empty(t, courses[0].Tasks)
	})

	t.Run("GetCourseByID", func(t *testing.T) {
		course, err := mockStore.GetCourseByID(1)

		assert.NoError(t, err)
		assert.Equal(t, 1, course.ID)
		assert.Equal(t, "SQL Injection", course.VulnerabilityType)
		assert.Equal(t, 2, len(course.Tasks))

		_, err = mockStore.GetCourseByID(999)
		assert.Error(t, err)
		assert.Equal(t, storage.ErrCourseNotFound, err)
	})
}

func TestMockStorage_UserProgress(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("GetUserProgress", func(t *testing.T) {
		progress, err := mockStore.GetUserProgress(1)

		assert.NoError(t, err)
		assert.Equal(t, 1, progress.UserID)
		assert.True(t, progress.Completed[1])

		progress, err = mockStore.GetUserProgress(999)

		assert.NoError(t, err)
		assert.Equal(t, 999, progress.UserID)
		assert.Empty(t, progress.Completed)
	})

	t.Run("CompleteTask", func(t *testing.T) {
		err := mockStore.CompleteTask(1, 2)
		assert.NoError(t, err)

		progress, _ := mockStore.GetUserProgress(1)
		assert.True(t, progress.Completed[2])

		err = mockStore.CompleteTask(1, 999)
		assert.Error(t, err)
		assert.Equal(t, storage.ErrTaskNotFound, err)
	})
}

func TestMockStorage_UserManagement(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("GetUserByUsername", func(t *testing.T) {
		user, err := mockStore.GetUserByUsername("admin")

		assert.NoError(t, err)
		assert.Equal(t, "admin", user.Username)
		assert.NotEmpty(t, user.Courses)

		_, err = mockStore.GetUserByUsername("nonexistent")
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("GetUserByID", func(t *testing.T) {
		user, err := mockStore.GetUserByID(1)

		assert.NoError(t, err)
		assert.Equal(t, 1, user.ID)
		assert.Equal(t, "admin", user.Username)
		assert.NotEmpty(t, user.Courses)

		_, err = mockStore.GetUserByID(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("UpdateUserLastLogin", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(1)
		oldLogin := user.LastLogin

		time.Sleep(10 * time.Millisecond)

		err := mockStore.UpdateUserLastLogin(1)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(1)
		assert.True(t, user.LastLogin.After(oldLogin))

		err = mockStore.UpdateUserLastLogin(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})
}

func TestMockStorage_2FAAndOTP(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("Enable2FA", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(2)
		assert.False(t, user.Is2FAEnabled)

		err := mockStore.Enable2FA(2)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(2)
		assert.True(t, user.Is2FAEnabled)

		err = mockStore.Enable2FA(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("OTP Flow", func(t *testing.T) {
		err := mockStore.SaveOTPCode(1, "123456")
		assert.NoError(t, err)

		valid, err := mockStore.VerifyOTPCode(1, "123456")
		assert.NoError(t, err)
		assert.True(t, valid)

		valid, err = mockStore.VerifyOTPCode(1, "wrong")
		assert.NoError(t, err)
		assert.False(t, valid)

		err = mockStore.ClearOTPCode(1)
		assert.NoError(t, err)

		valid, err = mockStore.VerifyOTPCode(1, "123456")
		assert.NoError(t, err)
		assert.False(t, valid)

		err = mockStore.SaveOTPCode(999, "123456")
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})
}

func TestMockStorage_UserAdministration(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("IsAdmin", func(t *testing.T) {
		isAdmin, err := mockStore.IsAdmin(1)
		assert.NoError(t, err)
		assert.True(t, isAdmin)

		isAdmin, err = mockStore.IsAdmin(2)
		assert.NoError(t, err)
		assert.False(t, isAdmin)

		_, err = mockStore.IsAdmin(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("GetAllUsers", func(t *testing.T) {
		users, err := mockStore.GetAllUsers()

		assert.NoError(t, err)
		assert.GreaterOrEqual(t, len(users), 2)
	})

	t.Run("GetUsersByRole", func(t *testing.T) {
		admins, err := mockStore.GetUsersByRole(true)
		assert.NoError(t, err)
		for _, admin := range admins {
			assert.True(t, admin.IsAdmin)
		}

		regularUsers, err := mockStore.GetUsersByRole(false)
		assert.NoError(t, err)
		for _, user := range regularUsers {
			assert.False(t, user.IsAdmin)
		}
	})

	t.Run("SearchUsers", func(t *testing.T) {
		users, err := mockStore.SearchUsers("admin")
		assert.NoError(t, err)
		assert.NotEmpty(t, users)
		assert.Equal(t, "admin", users[0].Username)

		users, err = mockStore.SearchUsers("example.com")
		assert.NoError(t, err)
		assert.NotEmpty(t, users)

		users, err = mockStore.SearchUsers("Regular")
		assert.NoError(t, err)
		assert.NotEmpty(t, users)

		users, err = mockStore.SearchUsers("nonexistent")
		assert.NoError(t, err)
		assert.Empty(t, users)
	})
}

func TestMockStorage_UserProfileManagement(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("UpdateUserProfile", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(2)
		originalEmail := user.Email
		originalName := user.FullName

		updateReq := models.UpdateProfileRequest{
			Email:    "updated@example.com",
			FullName: "Updated Name",
		}

		err := mockStore.UpdateUserProfile(2, updateReq)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(2)
		assert.Equal(t, "updated@example.com", user.Email)
		assert.Equal(t, "Updated Name", user.FullName)

		mockStore.UpdateUserProfile(2, models.UpdateProfileRequest{
			Email:    originalEmail,
			FullName: originalName,
		})

		err = mockStore.UpdateUserProfile(999, updateReq)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("UpdateUserStatus", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(2)
		originalStatus := user.IsActive

		err := mockStore.UpdateUserStatus(2, !originalStatus)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(2)
		assert.Equal(t, !originalStatus, user.IsActive)

		mockStore.UpdateUserStatus(2, originalStatus)

		err = mockStore.UpdateUserStatus(999, true)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("UpdateUserProfileImage", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(2)
		originalImage := user.ProfileImage

		err := mockStore.UpdateUserProfileImage(2, "/new/image/path.jpg")
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(2)
		assert.Equal(t, "/new/image/path.jpg", user.ProfileImage)

		mockStore.UpdateUserProfileImage(2, originalImage)

		err = mockStore.UpdateUserProfileImage(999, "/any/path.jpg")
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})
}

func TestMockStorage_AdminUserRoles(t *testing.T) {
	mockStore := new(storage.MockStorage)

	t.Run("PromoteToAdmin", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(2)
		assert.False(t, user.IsAdmin)

		err := mockStore.PromoteToAdmin(2)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(2)
		assert.True(t, user.IsAdmin)

		err = mockStore.PromoteToAdmin(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})

	t.Run("DemoteFromAdmin", func(t *testing.T) {
		user, _ := mockStore.GetUserByID(1)
		assert.True(t, user.IsAdmin)

		err := mockStore.DemoteFromAdmin(1)
		assert.NoError(t, err)

		user, _ = mockStore.GetUserByID(1)
		assert.False(t, user.IsAdmin)

		mockStore.PromoteToAdmin(1)

		err = mockStore.DemoteFromAdmin(999)
		assert.Error(t, err)
		assert.Contains(t, err.Error(), "user not found")
	})
}

func TestMockStorage_DeleteUser(t *testing.T) {
	mockStore := new(storage.MockStorage)

	tempUser := models.User{
		Username:     "temp_user",
		PasswordHash: "hashedpassword",
		Email:        "temp@example.com",
		FullName:     "Temporary User",
	}

	mockStore.CreateUser(tempUser)

	user, _ := mockStore.GetUserByUsername("temp_user")
	tempUserID := user.ID

	err := mockStore.DeleteUser(tempUserID)
	assert.NoError(t, err)

	_, err = mockStore.GetUserByID(tempUserID)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "user not found")

	err = mockStore.DeleteUser(999)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "user not found")
}
