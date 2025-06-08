package models

import (
	"time"
)

type User struct {
	ID             int              `json:"id"`
	Username       string           `json:"username"`
	PasswordHash   string           `json:"-"` // Скрыто в JSON
	Email          string           `json:"email"`
	FullName       string           `json:"fullName"`
	ProfileImage   string           `json:"profileImage,omitempty"`
	TOTPSecret     string           `json:"-"` // Скрыто в JSON
	Is2FAEnabled   bool             `json:"is2faEnabled"`
	IsAdmin        bool             `json:"isAdmin,omitempty"`
	IsActive       bool             `json:"isActive,omitempty"`
	IsTeacher      bool             `json:"isTeacher,omitempty"`
	IsDeleted      bool             `json:"-"` // Скрыто в JSON
	LastLogin      time.Time        `json:"lastLogin,omitempty"`
	Courses        []CourseProgress `json:"courses,omitempty"`
	CompletedTasks int              `json:"completedTasks,omitempty"`
	TotalTasks     int              `json:"totalTasks,omitempty"`
	Progress       float64          `json:"progress,omitempty"`
}

type CourseProgress struct {
	ID                int     `json:"id"`
	VulnerabilityType string  `json:"vulnerabilityType"`
	Description       string  `json:"description"`
	TasksCount        int     `json:"tasksCount"`
	CompletedTasks    int     `json:"completedTasks"`
	Progress          float64 `json:"progress"`
}

type Course struct {
	ID                int    `json:"id"`
	VulnerabilityType string `json:"vulnerabilityType"`
	TasksCount        int    `json:"tasksCount"`
	Description       string `json:"description"`
	Tasks             []Task `json:"tasks"`
}

type Task struct {
	ID          int    `json:"id"`
	CourseID    int    `json:"courseId"`
	Title       string `json:"title"`
	Description string `json:"description"`
	Difficulty  string `json:"difficulty"`
	Order       int    `json:"order"`
	Points      int    `json:"points"`
	Content     string `json:"content"`
	Solution    string `json:"solution"`
	IsCompleted bool   `json:"isCompleted"`
}

type UserProgress struct {
	UserID    int          `json:"userId"`
	Completed map[int]bool `json:"completed"`
}

type TaskSubmission struct {
	ID          int       `json:"id"`
	UserID      int       `json:"user_id"`
	TaskID      int       `json:"task_id"`
	Answer      string    `json:"answer"`
	Attachments []string  `json:"attachments,omitempty"`
	SubmittedAt time.Time `json:"submitted_at"`
	Status      string    `json:"status"`
	CourseID    int       `json:"courseID"`
}

type TaskSubmissionResponse struct {
	SubmissionID int       `json:"submission_id"`
	TaskID       int       `json:"task_id"`
	Status       string    `json:"status"`
	SubmittedAt  time.Time `json:"submitted_at"`
	Message      string    `json:"message,omitempty"`
	IsCorrect    bool      `json:"is_correct"`
}

type TaskSubmissionDetails struct {
	SubmissionID int       `json:"submission_id"`
	TaskID       int       `json:"task_id"`
	TaskTitle    string    `json:"task_title"`
	CourseID     int       `json:"course_id"`
	CourseName   string    `json:"course_name"`
	SubmittedAt  time.Time `json:"submitted_at"`
	GradedAt     time.Time `json:"graded_at,omitempty"`
	Status       string    `json:"status"`
	Score        float64   `json:"score,omitempty"`
	MaxScore     float64   `json:"max_score"`
	Feedback     string    `json:"feedback,omitempty"`
}

type GradeSubmission struct {
	Score    float64   `json:"score" binding:"required"`
	Feedback string    `json:"feedback"`
	GradedAt time.Time `json:"graded_at,omitempty"`
	GradedBy int       `json:"graded_by,omitempty"`
}

type CourseStatistics struct {
	CourseID            int     `json:"course_id"`
	CourseName          string  `json:"course_name"`
	EnrolledStudents    int     `json:"enrolled_students"`
	CompletedStudents   int     `json:"completed_students"`
	AverageCompletion   float64 `json:"average_completion_percentage"`
	AverageScore        float64 `json:"average_score"`
	TaskCompletionRates []struct {
		TaskID       int     `json:"task_id"`
		TaskTitle    string  `json:"task_title"`
		CompletedBy  int     `json:"completed_by"`
		SuccessRate  float64 `json:"success_rate"`
		AverageScore float64 `json:"average_score"`
	} `json:"task_completion_rates"`
	StudentsProgress []struct {
		UserID            int     `json:"user_id"`
		Username          string  `json:"username"`
		CompletionPercent float64 `json:"completion_percentage"`
		AverageScore      float64 `json:"average_score"`
		LastActivity      string  `json:"last_activity"`
	} `json:"students_progress"`
}

type UserStatistics struct {
	UserID           int       `json:"user_id"`
	TotalCourses     int       `json:"total_courses"`
	CompletedCourses int       `json:"completed_courses"`
	TotalTasks       int       `json:"total_tasks"`
	CompletedTasks   int       `json:"completed_tasks"`
	AverageScore     float64   `json:"average_score"`
	TotalPoints      int       `json:"total_points"`
	JoinedDate       time.Time `json:"joined_date"`
	LastActive       time.Time `json:"last_active"`
	CoursesProgress  []struct {
		CourseID          int     `json:"course_id"`
		CourseName        string  `json:"course_name"`
		CompletionPercent float64 `json:"completion_percentage"`
		AverageScore      float64 `json:"average_score"`
		LastActivity      string  `json:"last_activity"`
	} `json:"courses_progress"`
}

type LearningEffectiveness struct {
	Period            string  `json:"period"`
	CourseID          int     `json:"course_id,omitempty"`
	CourseName        string  `json:"course_name,omitempty"`
	TotalStudents     int     `json:"total_students"`
	AverageCompletion float64 `json:"average_completion_percentage"`
	AverageGrade      float64 `json:"average_grade"`
	CompletionRate    float64 `json:"completion_rate"`
	DropoutRate       float64 `json:"dropout_rate"`
	AverageTimeSpent  string  `json:"average_time_spent"`
	DifficultTasks    []struct {
		TaskID       int     `json:"task_id"`
		TaskTitle    string  `json:"task_title"`
		FailRate     float64 `json:"fail_rate"`
		AverageScore float64 `json:"average_score"`
		AverageTime  string  `json:"average_time"`
	} `json:"difficult_tasks"`
	EffectivenessScore float64 `json:"effectiveness_score"`
}

type LeaderboardEntry struct {
	Position     int     `json:"position"`
	UserID       int     `json:"user_id"`
	Username     string  `json:"username"`
	Points       int     `json:"points"`
	Completed    int     `json:"completed_tasks"`
	AverageScore float64 `json:"average_score"`
}

type LearningActivity struct {
	ID           int       `json:"id,omitempty"`
	UserID       int       `json:"user_id"`
	CourseID     int       `json:"course_id"`
	TaskID       int       `json:"task_id,omitempty"`
	ActivityType string    `json:"activity_type"`
	Duration     int       `json:"duration_seconds,omitempty"`
	Timestamp    time.Time `json:"timestamp"`
	Details      string    `json:"details,omitempty"`
}

type LearningPath struct {
	UserID          int       `json:"user_id"`
	GeneratedAt     time.Time `json:"generated_at"`
	Recommendations []struct {
		CourseID      int    `json:"course_id"`
		CourseName    string `json:"course_name"`
		Priority      int    `json:"priority"`
		Reason        string `json:"reason"`
		EstimatedTime string `json:"estimated_time"`
	} `json:"recommendations"`
	NextTasks []struct {
		TaskID     int    `json:"task_id"`
		TaskTitle  string `json:"task_title"`
		CourseID   int    `json:"course_id"`
		CourseName string `json:"course_name"`
		Priority   int    `json:"priority"`
		DueDate    string `json:"due_date,omitempty"`
	} `json:"next_tasks"`
	Skills []struct {
		SkillName       string  `json:"skill_name"`
		CurrentLevel    int     `json:"current_level"`
		Progress        float64 `json:"progress_to_next_level"`
		RecommendedTask int     `json:"recommended_task_id,omitempty"`
	} `json:"skills"`
}

type UpdateProfileRequest struct {
	Email    string `json:"email,omitempty"`
	Username string `json:"username,omitempty"`
	FullName string `json:"fullName,omitempty"`
	Password string `json:"password,omitempty"`
}

type UpdateStatusRequest struct {
	IsActive bool `json:"isActive" binding:"required"`
}

type ChangePasswordRequest struct {
	CurrentPassword string `json:"currentPassword" binding:"required,min=6"`
	NewPassword     string `json:"newPassword" binding:"required,min=6"`
}

type ForgotPasswordRequest struct {
	Email    string `json:"email"`
	Username string `json:"username"`
}

type ResetPasswordRequest struct {
	TempToken   string `json:"tempToken" binding:"required"`
	Code        string `json:"code" binding:"required"`
	NewPassword string `json:"newPassword" binding:"required,min=6"`
}

type LoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type LoginResponse struct {
	Token    string `json:"token"`
	UserID   int    `json:"userId"`
	Username string `json:"username"`
	Email    string `json:"email"`
}

type RegisterRequest struct {
	Username  string `json:"username" binding:"required" example:"newuser"`
	Password  string `json:"password" binding:"required" example:"newpassword123"`
	Email     string `json:"email" binding:"required" example:"user@example.com"`
	FullName  string `json:"fullName" binding:"required" example:"New User"`
	IsTeacher bool   `json:"isTeacher" binding:"required"`
}

type RegisterResponse struct {
	Token   string `json:"token"`
	Message string `json:"message"`
}

type VerifyOTPRequest struct {
	TempToken string `json:"tempToken" binding:"required"`
	OTP       string `json:"otp" binding:"required"`
}

type Enable2FARequest struct {
	OTP string `json:"otp" binding:"required" example:"123456"`
}

type ErrorResponse struct {
	Error string `json:"error"`
}

type SuccessResponse struct {
	Message string `json:"message"`
}

type TempTokenResponse struct {
	TempToken string `json:"tempToken"`
	Message   string `json:"message"`
}

type Enable2FAResponse struct {
	Status string `json:"status" example:"2FA enabled"`
}

type DeleteAccountInitRequest struct {
	Password string `json:"password" binding:"required"`
}

type DeleteAccountConfirmRequest struct {
	Code string `json:"code" binding:"required,len=6"`
}
