package storage

import (
	"errors"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend-svc/models"
	"strings"
	"time"
)

// Моковые данные для тестирования
var (
	mockCourses = []models.Course{
		{ID: 1, Title: "Go Basics", Description: "Learn the basics of Go programming."},
		{ID: 2, Title: "Advanced Go", Description: "Deep dive into Go."},
		{ID: 3, Title: "Web Development with Go", Description: "Build web applications using Go."},
	}

	mockAssignments = []models.Assignment{
		{ID: 1, CourseID: 1, Title: "Introduction to Go", Description: "Learn about Go's history and features."},
		{ID: 2, CourseID: 1, Title: "Variables and Types", Description: "Understand Go's type system."},
		{ID: 3, CourseID: 2, Title: "Concurrency", Description: "Master goroutines and channels."},
		{ID: 4, CourseID: 3, Title: "Building a REST API", Description: "Create a REST API with Gin."},
	}

	mockUserProgress = map[int]models.UserProgress{
		1: {UserID: 1, Completed: map[int]bool{1: true, 2: true}},
		2: {UserID: 2, Completed: map[int]bool{1: true}},
	}

	mockUsers = map[int]models.User{
		1: {
			ID:           1,
			Username:     "admin",
			PasswordHash: "$2a$10$XJaM5WKk3xQQbUgRIl9YGuRzJtfuZ/lsGQKJQL9AVG1cAP5DJFuTa", // "admin123"
			Email:        "eyuborisova@yandex.ru",
			FullName:     "Admin User",
			TOTPSecret:   "JBSWY3DPEHPK3PXP",
			Is2FAEnabled: true,
		},
		2: {
			ID:           2,
			Username:     "user123",
			PasswordHash: "$2a$10$qJ7EYH.1r9kQQBqS7iPFm.LoTJHEbzGP4.94a8QCbeHEFjyFsHoKG", // "pass123"
			Email:        "user@example.com",
			FullName:     "Regular User",
			TOTPSecret:   "JBSWY3DPEHPK3PXP",
			Is2FAEnabled: false,
		},
	}

	mockUsersByUsername = map[string]int{
		"admin":   1,
		"user123": 2,
	}
)

// GetCourses возвращает список всех курсов
func (s *MockStorage) GetCourses() ([]models.Course, error) {
	return mockCourses, nil
}

// GetCourseByID возвращает курс по ID
func (s *MockStorage) GetCourseByID(id int) (models.Course, error) {
	for _, course := range mockCourses {
		if course.ID == id {
			return course, nil
		}
	}
	return models.Course{}, errors.New("course not found")
}

// GetUserProgress возвращает прогресс пользователя
func (s *MockStorage) GetUserProgress(userID int) (models.UserProgress, error) {
	progress, exists := mockUserProgress[userID]
	if !exists {
		// Если прогресса нет, создаем пустой
		return models.UserProgress{
			UserID:    userID,
			Completed: make(map[int]bool),
		}, nil
	}
	return progress, nil
}

// CompleteAssignment отмечает задание как выполненное
func (s *MockStorage) CompleteAssignment(userID, assignmentID int) error {
	// Проверяем, что задание существует
	var assignmentExists bool
	for _, a := range mockAssignments {
		if a.ID == assignmentID {
			assignmentExists = true
			break
		}
	}

	if !assignmentExists {
		return errors.New("assignment not found")
	}

	// Получаем или создаем прогресс пользователя
	progress, exists := mockUserProgress[userID]
	if !exists {
		progress = models.UserProgress{
			UserID:    userID,
			Completed: make(map[int]bool),
		}
	}

	// Отмечаем задание как выполненное
	progress.Completed[assignmentID] = true
	mockUserProgress[userID] = progress

	return nil
}

// CreateUser создает нового пользователя
func (s *MockStorage) CreateUser(user models.User) error {
	// Проверяем, что пользователя с таким именем еще нет
	if _, exists := mockUsersByUsername[user.Username]; exists {
		return errors.New("username already exists")
	}

	// Генерируем новый ID
	newID := len(mockUsers) + 1
	user.ID = newID

	// Сохраняем пользователя
	mockUsers[newID] = user
	mockUsersByUsername[user.Username] = newID

	return nil
}

// GetUserByUsername возвращает пользователя по имени пользователя
func (s *MockStorage) GetUserByUsername(username string) (models.User, error) {
	userID, exists := mockUsersByUsername[username]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	user, exists := mockUsers[userID]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	return user, nil
}

// GetUserByID возвращает пользователя по ID
func (s *MockStorage) GetUserByID(id int) (models.User, error) {
	user, exists := mockUsers[id]
	if !exists {
		return models.User{}, errors.New("user not found")
	}

	return user, nil
}

// UpdateUserLastLogin обновляет время последнего входа пользователя
func (s *MockStorage) UpdateUserLastLogin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	// В моковой реализации мы просто отмечаем, что обновление произошло
	user.LastLogin = time.Now()
	mockUsers[userID] = user

	return nil
}

// Enable2FA включает двухфакторную аутентификацию для пользователя
func (s *MockStorage) Enable2FA(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}

	user.Is2FAEnabled = true
	mockUsers[userID] = user

	return nil
}

// IsAdmin проверяет, является ли пользователь администратором
func (s *MockStorage) IsAdmin(userID int) (bool, error) {
	// В моковой реализации только пользователь с ID=1 является администратором
	return userID == 1, nil
}

// GetAllUsers возвращает список всех пользователей из моковых данных
func (s *MockStorage) GetAllUsers() ([]models.User, error) {
	var users []models.User
	for _, user := range mockUsers {
		users = append(users, user)
	}
	return users, nil
}

// UpdateUserProfile обновляет профиль пользователя в моковых данных
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

// GetUsersByRole возвращает список пользователей с определенной ролью из моковых данных
func (s *MockStorage) GetUsersByRole(isAdmin bool) ([]models.User, error) {
	var users []models.User
	for _, user := range mockUsers {
		if user.IsAdmin == isAdmin {
			users = append(users, user)
		}
	}
	return users, nil
}

// SearchUsers ищет пользователей по имени пользователя, email или полному имени в моковых данных
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

// UpdateUserStatus обновляет статус пользователя в моковых данных
func (s *MockStorage) UpdateUserStatus(userID int, isActive bool) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsActive = isActive
	mockUsers[userID] = user
	return nil
}

// PromoteToAdmin повышает пользователя до администратора в моковых данных
func (s *MockStorage) PromoteToAdmin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsAdmin = true
	mockUsers[userID] = user
	return nil
}

// DemoteFromAdmin понижает пользователя с роли администратора в моковых данных
func (s *MockStorage) DemoteFromAdmin(userID int) error {
	user, exists := mockUsers[userID]
	if !exists {
		return errors.New("user not found")
	}
	user.IsAdmin = false
	mockUsers[userID] = user
	return nil
}

func (s *MockStorage) ClearOTPCode(userID int) error {
	return nil
}

func (s *MockStorage) VerifyOTPCode(userID int, code string) (bool, error) {
	return true, nil
}

func (s *MockStorage) SaveOTPCode(userID int, code string) error {
	return nil
}
