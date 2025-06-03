package storage

import (
	"errors"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend-svc/models"
	"strings"
	"time"
)

var (
	mockCourses = []models.Course{
		{
			ID:                1,
			VulnerabilityType: "SQL Injection",
			TasksCount:        2,
			Description:       "Learn about SQL injection vulnerabilities",
			Tasks: []models.Task{
				{ID: 1, CourseID: 1, Title: "Basics of SQL Injection", Description: "Understanding the fundamentals", Difficulty: "easy", Order: 1},
				{ID: 2, CourseID: 1, Title: "Advanced SQL Injection", Description: "More complex techniques", Difficulty: "medium", Order: 2},
			},
		},
		{
			ID:                2,
			VulnerabilityType: "XSS",
			TasksCount:        1,
			Description:       "Cross-site scripting attacks and prevention",
			Tasks: []models.Task{
				{ID: 3, CourseID: 2, Title: "XSS in Web Applications", Description: "Exploiting front-end vulnerabilities", Difficulty: "medium", Order: 1},
			},
		},
		{
			ID:                3,
			VulnerabilityType: "CSRF",
			TasksCount:        1,
			Description:       "Cross-site request forgery attacks",
			Tasks: []models.Task{
				{ID: 4, CourseID: 3, Title: "Understanding CSRF", Description: "Forging requests across sites", Difficulty: "hard", Order: 1},
			},
		},
	}

	mockTasks = []models.Task{
		{ID: 1, CourseID: 1, Title: "Basics of SQL Injection", Description: "Understanding the fundamentals", Difficulty: "easy", Order: 1},
		{ID: 2, CourseID: 1, Title: "Advanced SQL Injection", Description: "More complex techniques", Difficulty: "medium", Order: 2},
		{ID: 3, CourseID: 2, Title: "XSS in Web Applications", Description: "Exploiting front-end vulnerabilities", Difficulty: "medium", Order: 1},
		{ID: 4, CourseID: 3, Title: "Understanding CSRF", Description: "Forging requests across sites", Difficulty: "hard", Order: 1},
	}

	mockUserProgress = map[int]models.UserProgress{
		1: {
			UserID: 1,
			Completed: map[int]bool{
				1: true,
			},
		},
		2: {
			UserID: 2,
			Completed: map[int]bool{
				1: true,
				2: true,
			},
		},
	}

	mockUsers = map[int]models.User{
		1: {
			ID:             1,
			Username:       "admin",
			PasswordHash:   "$2a$10$XJaM5WKk3xQQbUgRIl9YGuRzJtfuZ/lsGQKJQL9AVG1cAP5DJFuTa",
			Email:          "eyuborisova@yandex.ru",
			FullName:       "Admin User",
			ProfileImage:   "/uploads/avatars/admin.jpg",
			TOTPSecret:     "JBSWY3DPEHPK3PXP",
			Is2FAEnabled:   true,
			IsAdmin:        true,
			IsActive:       true,
			LastLogin:      time.Now().Add(-24 * time.Hour),
			CompletedTasks: 1,
			TotalTasks:     4,
			Progress:       25.0,
		},
		2: {
			ID:             2,
			Username:       "user123",
			PasswordHash:   "$2a$10$mY1j/T1JlJ7H50omhthPluBS2qYiU/r64X8C6UXEkbobiEDC9UZ62",
			Email:          "user@example.com",
			FullName:       "Regular User",
			ProfileImage:   "",
			TOTPSecret:     "JBSWY3DPEHPK3PXP",
			Is2FAEnabled:   false,
			IsAdmin:        false,
			IsActive:       true,
			LastLogin:      time.Now().Add(-2 * time.Hour),
			CompletedTasks: 2,
			TotalTasks:     4,
			Progress:       50.0,
		},
	}

	mockUsersByUsername = map[string]int{
		"admin":   1,
		"user123": 2,
	}
)

func (s *MockStorage) GetCourses() ([]models.Course, error) {
	coursesWithoutTasks := make([]models.Course, len(mockCourses))

	for i, course := range mockCourses {
		coursesWithoutTasks[i] = models.Course{
			ID:                course.ID,
			VulnerabilityType: course.VulnerabilityType,
			TasksCount:        course.TasksCount,
			Description:       course.Description,
		}
	}

	return coursesWithoutTasks, nil
}

func (s *MockStorage) GetCourseByID(id int) (models.Course, error) {
	for _, course := range mockCourses {
		if course.ID == id {
			return course, nil
		}
	}
	return models.Course{}, ErrCourseNotFound
}

func (s *MockStorage) GetUserProgress(userID int) (models.UserProgress, error) {
	progress, exists := mockUserProgress[userID]
	if !exists {
		return models.UserProgress{
			UserID:    userID,
			Completed: make(map[int]bool),
		}, nil
	}
	return progress, nil
}

func (s *MockStorage) CompleteTask(userID, taskID int) error {
	var taskExists bool
	for _, t := range mockTasks {
		if t.ID == taskID {
			taskExists = true
			break
		}
	}

	if !taskExists {
		return ErrTaskNotFound
	}

	progress, exists := mockUserProgress[userID]
	if !exists {
		progress = models.UserProgress{
			UserID:    userID,
			Completed: make(map[int]bool),
		}
	}

	progress.Completed[taskID] = true
	mockUserProgress[userID] = progress

	user, userExists := mockUsers[userID]
	if userExists && !progress.Completed[taskID] {
		user.CompletedTasks++
		user.Progress = float64(user.CompletedTasks) / float64(user.TotalTasks) * 100
		mockUsers[userID] = user
	}

	return nil
}

func (s *MockStorage) CreateUser(user models.User) error {
	if _, exists := mockUsersByUsername[user.Username]; exists {
		return errors.New("username already exists")
	}

	newID := len(mockUsers) + 1
	user.ID = newID
	user.TotalTasks = len(mockTasks)
	user.Progress = 0
	user.IsActive = true

	mockUsers[newID] = user
	mockUsersByUsername[user.Username] = newID

	return nil
}

func (s *MockStorage) GetUserByUsername(username string) (models.User, error) {
	userID, exists := mockUsersByUsername[username]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	user, exists := mockUsers[userID]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	courses, _ := s.GetCourses()
	coursesWithProgress := make([]models.CourseProgress, 0, len(courses))

	progress, _ := s.GetUserProgress(user.ID)

	for _, course := range courses {
		courseData, _ := s.GetCourseByID(course.ID)

		courseProgress := models.CourseProgress{
			ID:                courseData.ID,
			VulnerabilityType: courseData.VulnerabilityType,
			Description:       courseData.Description,
			TasksCount:        courseData.TasksCount,
		}

		for _, task := range courseData.Tasks {
			if completed, exists := progress.Completed[task.ID]; exists && completed {
				courseProgress.CompletedTasks++
			}
		}

		if courseProgress.TasksCount > 0 {
			courseProgress.Progress = float64(courseProgress.CompletedTasks) / float64(courseProgress.TasksCount) * 100
		}

		coursesWithProgress = append(coursesWithProgress, courseProgress)
	}

	user.Courses = coursesWithProgress

	return user, nil
}

func (s *MockStorage) GetUserByID(id int) (models.User, error) {
	user, exists := mockUsers[id]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	courses, _ := s.GetCourses()
	coursesWithProgress := make([]models.CourseProgress, 0, len(courses))

	progress, _ := s.GetUserProgress(user.ID)

	for _, course := range courses {
		courseData, _ := s.GetCourseByID(course.ID)

		courseProgress := models.CourseProgress{
			ID:                courseData.ID,
			VulnerabilityType: courseData.VulnerabilityType,
			Description:       courseData.Description,
			TasksCount:        courseData.TasksCount,
		}

		for _, task := range courseData.Tasks {
			if completed, exists := progress.Completed[task.ID]; exists && completed {
				courseProgress.CompletedTasks++
			}
		}

		if courseProgress.TasksCount > 0 {
			courseProgress.Progress = float64(courseProgress.CompletedTasks) / float64(courseProgress.TasksCount) * 100
		}

		coursesWithProgress = append(coursesWithProgress, courseProgress)
	}

	user.Courses = coursesWithProgress

	return user, nil
}

func (s *MockStorage) UpdateUserLastLogin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	user.LastLogin = time.Now()
	mockUsers[userID] = user

	return nil
}

func (s *MockStorage) Enable2FA(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	user.Is2FAEnabled = true
	mockUsers[userID] = user

	return nil
}

func (s *MockStorage) IsAdmin(userID int) (bool, error) {
	user, exists := mockUsers[userID]
	if !exists {
		return false, errors.New("user not found")
	}
	return user.IsAdmin, nil
}

func (s *MockStorage) GetAllUsers() ([]models.User, error) {
	var users []models.User
	for _, user := range mockUsers {
		users = append(users, user)
	}
	return users, nil
}

func (s *MockStorage) UpdateUserProfile(userID int, data models.UpdateProfileRequest) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	if data.Email != "" {
		user.Email = data.Email
	}

	if data.FullName != "" {
		user.FullName = data.FullName
	}

	if data.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(data.Password), bcrypt.DefaultCost)
		if err != nil {
			return err
		}
		user.PasswordHash = string(hashedPassword)
	}

	mockUsers[userID] = user
	return nil
}

func (s *MockStorage) GetUsersByRole(isAdmin bool) ([]models.User, error) {
	var users []models.User
	for _, user := range mockUsers {
		if user.IsAdmin == isAdmin {
			users = append(users, user)
		}
	}
	return users, nil
}

func (s *MockStorage) SearchUsers(query string) ([]models.User, error) {
	query = strings.ToLower(query)
	var users []models.User
	for _, user := range mockUsers {
		if strings.Contains(strings.ToLower(user.Username), query) ||
			strings.Contains(strings.ToLower(user.Email), query) ||
			strings.Contains(strings.ToLower(user.FullName), query) {
			users = append(users, user)
		}
	}
	return users, nil
}

func (s *MockStorage) UpdateUserStatus(userID int, isActive bool) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsActive = isActive
	mockUsers[userID] = user
	return nil
}

func (s *MockStorage) PromoteToAdmin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsAdmin = true
	mockUsers[userID] = user
	return nil
}

func (s *MockStorage) DemoteFromAdmin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsAdmin = false
	mockUsers[userID] = user
	return nil
}

func (s *MockStorage) SaveOTPCode(userID int, code string) error {
	_, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	mockOTPCodes[userID] = struct {
		code      string
		expiresAt time.Time
	}{
		code:      code,
		expiresAt: time.Now().Add(5 * time.Minute),
	}

	return nil
}

func (s *MockStorage) VerifyOTPCode(userID int, code string) (bool, error) {
	stored, exists := mockOTPCodes[userID]
	if !exists {
		return false, nil
	}

	if time.Now().After(stored.expiresAt) {
		return false, nil
	}

	return stored.code == code, nil
}

func (s *MockStorage) ClearOTPCode(userID int) error {
	delete(mockOTPCodes, userID)
	return nil
}

var mockOTPCodes = make(map[int]struct {
	code      string
	expiresAt time.Time
})

func (s *MockStorage) UpdateUserProfileImage(userID int, imageURL string) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	user.ProfileImage = imageURL
	mockUsers[userID] = user

	return nil
}

func (s *MockStorage) DeleteUser(userID int) error {
	if _, exists := mockUsers[userID]; !exists {
		return errors.New("user not found")
	}

	delete(mockUsers, userID)

	for username, id := range mockUsersByUsername {
		if id == userID {
			delete(mockUsersByUsername, username)
			break
		}
	}

	delete(mockUserProgress, userID)

	return nil
}
