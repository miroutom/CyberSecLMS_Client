package storage

import (
	"lmsmodule/backend/models"
)

// Courses represents the list of available courses.
// @Title Courses
// @Description The list of courses available in the system.
var Courses = []models.Course{
	{ID: 1, Title: "Go Basics", Description: "Learn the basics of Go programming."},
	{ID: 2, Title: "Advanced Go", Description: "Deep dive into Go."},
}

// Assignments represents the list of assignments.
// @Title Assignments
// @Description The list of assignments associated with courses.
var Assignments = []models.Assignment{
	{ID: 1, CourseID: 1, Title: "Introduction to Go"},
	{ID: 2, CourseID: 1, Title: "Variables and Types"},
}

// UserProgress represents the progress of users.
// @Title User Progress
// @Description The progress of users in the courses.
var UserProgress = map[int]*models.UserProgress{
	1: {UserID: 1, Completed: map[int]bool{}, LastActivity: ""},
}

// Users represents the list of users.
// @Title Users
// @Description The list of users with their credentials.
var Users = map[string]models.User{
	"admin":    {Username: "admin", PasswordHash: "password"},
	"user123":  {Username: "user123", PasswordHash: "mypassword"},
	"testuser": {Username: "testuser", PasswordHash: "testpass"},
}
