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
				1: true, // Пользователь 1 выполнил задание 1
			},
		},
		2: {
			UserID: 2,
			Completed: map[int]bool{
				1: true, // Пользователь 2 выполнил задание 1
				2: true, // Пользователь 2 выполнил задание 2
			},
		},
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
