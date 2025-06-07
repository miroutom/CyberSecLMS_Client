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
	CompleteTask(userID, taskID int) error

	CreateUser(user models.User) error
	GetUserByUsername(username string) (models.User, error)
	GetUserByID(id int) (models.User, error)
	UpdateUserLastLogin(userID int) error
	UpdateUserProfile(userID int, data models.UpdateProfileRequest) error
	Enable2FA(userID int) error
	UpdateUserProfileImage(userID int, imageURL string) error
	DeleteUser(userID int) error

	IsTeacher(userID int) (bool, error)
	IsAdmin(userID int) (bool, error)
	GetAllUsers() ([]models.User, error)
	GetUsersByRole(isAdmin bool) ([]models.User, error)
	SearchUsers(query string) ([]models.User, error)
	UpdateUserStatus(userID int, isActive bool) error
	PromoteToAdmin(userID int) error
	DemoteFromAdmin(userID int) error

	SaveOTPCode(userID int, code string) error
	VerifyOTPCode(userID int, code string) (bool, error)
	ClearOTPCode(userID int) error

	CreateCourse(course models.Course) (models.Course, error)
	UpdateCourse(id int, course models.Course) (models.Course, error)
	DeleteCourse(id int) error
	CreateTask(courseID int, task models.Task) (models.Task, error)
	UpdateTask(courseID, taskID int, task models.Task) (models.Task, error)
	DeleteTask(courseID, taskID int) error

	SubmitTaskAnswer(submission models.TaskSubmission) (models.TaskSubmissionResponse, error)
	GetUserSubmissions(userID int) ([]models.TaskSubmissionDetails, error)
	GetCourseStatistics(courseID int) (models.CourseStatistics, error)
	GetUserStatistics(userID int) (models.UserStatistics, error)
	GetLeaderboard(courseID int, limit int) ([]models.LeaderboardEntry, error)
	GetUserLearningPath(userID int) (models.LearningPath, error)
}

// DBStorage имплементирует Storage используя реальную базу данных
type DBStorage struct {
	DB *sql.DB
}

// MockStorage имплементирует Storage используя моковые данные в памяти
type MockStorage struct{}
