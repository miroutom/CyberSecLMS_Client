package storage

import (
	"database/sql"
	"lmsmodule/backend-svc/models"
)

// Storage определяет интерфейс для работы с данными
type Storage interface {
	GetCourses() ([]models.Course, error)
	GetCourseByID(id int) (models.Course, error)
	GetUserProgress(userID int) (models.UserProgress, error)
	CompleteAssignment(userID, assignmentID int) error

	CreateUser(user models.User) error
	GetUserByUsername(username string) (models.User, error)
	GetUserByID(id int) (models.User, error)
	UpdateUserLastLogin(userID int) error
	Enable2FA(userID int) error
	IsAdmin(userID int) (bool, error)

	GetAllUsers() ([]models.User, error)
	UpdateUserProfile(userID int, data models.UpdateProfileRequest) error
	GetUsersByRole(isAdmin bool) ([]models.User, error)
	SearchUsers(query string) ([]models.User, error)
	UpdateUserStatus(userID int, isActive bool) error
	PromoteToAdmin(userID int) error
	DemoteFromAdmin(userID int) error

	SaveOTPCode(userID int, code string) error
	VerifyOTPCode(userID int, code string) (bool, error)
	ClearOTPCode(userID int) error
}

// DBStorage имплементирует Storage используя реальную базу данных
type DBStorage struct {
	DB *sql.DB
}

// MockStorage имплементирует Storage используя моковые данные в памяти
type MockStorage struct{}
