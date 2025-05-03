package models

// Course represents a learning course
// @Schema
type Course struct {
	ID          int    `json:"id"`
	Title       string `json:"title"`
	Description string `json:"description"`
}

// Assignment represents a course task
// @Schema
type Assignment struct {
	ID       int    `json:"id"`
	CourseID int    `json:"course_id"`
	Title    string `json:"title"`
}

// UserProgress tracks user completion status
// @Schema
type UserProgress struct {
	UserID       int          `json:"user_id"`
	Completed    map[int]bool `json:"completed"`
	LastActivity string       `json:"last_activity"`
}

// User represents system user credentials
// @Schema
type User struct {
	ID           int
	Username     string
	PasswordHash string
	Email        string
	FullName     string
	TOTPSecret   string
	Is2FAEnabled bool
}

// User_Data represents user data needed for frontend
// @Schema
type User_Data struct {
	ID       int
	Username string
	Email    string
	FullName string
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
	Username string `json:"username" binding:"required" example:"newuser"`
	Password string `json:"password" binding:"required" example:"newpassword123"`
	Email    string `json:"email" binding:"required" example:"user@example.com"`
	FullName string `json:"fullName" binding:"required" example:"New User"`
}

type VerifyOTPRequest struct {
	TempToken string `json:"tempToken" binding:"required"`
	OTP       string `json:"otp" binding:"required"`
}

type Enable2FARequest struct {
	OTP string `json:"otp" binding:"required"`
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
	Status string `json:"status"`
}
