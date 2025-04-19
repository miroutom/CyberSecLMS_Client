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
	Username string `json:"username"`
	Password string `json:"password"`
}
