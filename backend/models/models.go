package models

import (
	"time"
)

type User struct {
	ID           int
	Username     string
	PasswordHash string
	Email        string
	FullName     string
	TOTPSecret   string
	Is2FAEnabled bool
	IsAdmin      bool
	IsActive     bool
	LastLogin    time.Time
}

type UserProfile struct {
	ID           int       `json:"id"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	FullName     string    `json:"fullName"`
	Is2FAEnabled bool      `json:"is2faEnabled"`
	IsAdmin      bool      `json:"isAdmin,omitempty"`   // Только для админов
	IsActive     bool      `json:"isActive,omitempty"`  // Только для админов
	LastLogin    time.Time `json:"lastLogin,omitempty"` // Только для админов
}

type UpdateProfileRequest struct {
	Email    string `json:"email,omitempty"`
	FullName string `json:"fullName,omitempty"`
	Password string `json:"password,omitempty"`
}

type UpdateStatusRequest struct {
	IsActive bool `json:"isActive" binding:"required"`
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

type Course struct {
	ID          int    `json:"id"`
	Title       string `json:"title"`
	Description string `json:"description"`
}

type Assignment struct {
	ID          int    `json:"id"`
	CourseID    int    `json:"courseId"`
	Title       string `json:"title"`
	Description string `json:"description"`
}

type UserProgress struct {
	UserID    int          `json:"userId"`
	Completed map[int]bool `json:"completed"`
}
