package models

// Course represents the information about a course.
type Course struct {
	ID          int    `json:"id"`          // Unique identifier for the course.
	Title       string `json:"title"`       // Title of the course.
	Description string `json:"description"` // Brief description of the course.
}

// Assignment represents a task or assignment linked to a specific course.
type Assignment struct {
	ID       int    `json:"id"`        // Unique identifier for the assignment.
	CourseID int    `json:"course_id"` // Identifier of the course this assignment belongs to.
	Title    string `json:"title"`     // Title of the assignment.
}

// UserProgress tracks the progress of a user in courses and assignments.
type UserProgress struct {
	UserID       int          `json:"user_id"`       // Unique identifier for the user.
	Completed    map[int]bool `json:"completed"`     // Map of completed assignments: key is the assignment ID, value is whether it is completed.
	LastActivity string       `json:"last_activity"` // Timestamp of the user's last activity (e.g., in ISO 8601 format).
}

// User represents a system user with authentication credentials.
type User struct {
	Username string `json:"username"` // Unique username of the user.
	Password string `json:"password"` // User's password (stored in plain text for mock purposes, should be hashed in production).
}
