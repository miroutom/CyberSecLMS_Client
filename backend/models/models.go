package models

// Course
// @Schema models.Course
// @Title Course
// @Description Information about a course.
// @Property ID int The unique identifier for the course.
// @Property Title string The title of the course.
// @Property Description string The brief description of the course.
type Course struct {
	ID          int    `json:"id"`
	Title       string `json:"title"`
	Description string `json:"description"`
}

// Assignment
// @Schema models.Assignment
// @Title Assignment
// @Description A task or assignment linked to a specific course.
// @Property ID int The unique identifier for the assignment.
// @Property CourseID int Identifier of the course this assignment belongs to.
// @Property Title string Title of the assignment.
type Assignment struct {
	ID       int    `json:"id"`
	CourseID int    `json:"course_id"`
	Title    string `json:"title"`
}

// UserProgress
// @Schema models.UserProgress
// @Title UserProgress
// @Description Tracks the progress of a user in courses and assignments.
// @Property UserID int Unique identifier for the user.
// @Property Completed map[int]bool Map of completed assignments: key is the assignment ID, value is whether it is completed.
// @Property LastActivity string Timestamp of the user's last activity (e.g., in ISO 8601 format).
type UserProgress struct {
	UserID       int          `json:"user_id"`       // Unique identifier for the user.
	Completed    map[int]bool `json:"completed"`     // Map of completed assignments: key is the assignment ID, value is whether it is completed.
	LastActivity string       `json:"last_activity"` // Timestamp of the user's last activity (e.g., in ISO 8601 format).
}

// User
// @Schema models.User
// @Title User
// @Description Represents a system user with authentication credentials.
// @Property Username string Unique username of the user.
// @Property Password string User's password (stored in plain text for mock purposes, should be hashed in production).
type User struct {
	Username string `json:"username"` // Unique username of the user.
	Password string `json:"password"` // User's password (stored in plain text for mock purposes, should be hashed in production).
}
