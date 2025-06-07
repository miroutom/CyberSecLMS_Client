package handlers

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/backend-svc/models"
	"net/http"
	"strconv"
	"time"
)

// GetCourses
// @Summary Get all courses
// @Tags Courses
// @Produce json
// @Success 200 {array} models.Course
// @Failure 500 {object} models.ErrorResponse
// @Router /courses [get]
func GetCourses(c *gin.Context) {
	courses, err := Store.GetCourses()
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, courses)
}

// GetCourseByID
// @Summary Get course by ID
// @Tags Courses
// @Produce json
// @Param id path int true "Course ID"
// @Success 200 {object} models.Course
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /courses/{id} [get]
func GetCourseByID(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	course, err := Store.GetCourseByID(id)
	if err != nil {
		c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Course not found"})
		return
	}

	c.JSON(http.StatusOK, course)
}

// GetUserProgress
// @Summary Get user progress
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.UserProgress
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id} [get]
func GetUserProgress(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	progress, err := Store.GetUserProgress(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, progress)
}

// CompleteTask
// @Summary Complete task
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Param task_id path int true "Task ID"
// @Success 200 {object} models.SuccessResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/tasks/{task_id}/complete [post]
func CompleteTask(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	taskID, err := strconv.Atoi(c.Param("task_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid task ID"})
		return
	}

	currentUserID, exists := c.Get("userID")
	if !exists || userID != currentUserID.(int) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	err = Store.CompleteTask(userID, taskID)
	if err != nil {
		if err.Error() == "task not found" {
			c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Task not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to complete task"})
		return
	}

	c.JSON(http.StatusOK, models.SuccessResponse{Message: "Task completed successfully"})
}

// SubmitTaskWithAnswer
// @Summary Submit task with answer for grading
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Param task_id path int true "Task ID"
// @Param submission body models.TaskSubmission true "Task submission"
// @Success 200 {object} models.TaskSubmissionResponse
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/tasks/{task_id}/submit [post]
func SubmitTaskWithAnswer(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	taskID, err := strconv.Atoi(c.Param("task_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid task ID"})
		return
	}

	currentUserID, exists := c.Get("userID")
	if !exists || userID != currentUserID.(int) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	var submission models.TaskSubmission
	if err := c.BindJSON(&submission); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid submission data"})
		return
	}

	submission.UserID = userID
	submission.TaskID = taskID
	submission.SubmittedAt = time.Now()

	result, err := Store.SubmitTaskAnswer(submission)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to submit task: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

// GetUserSubmissions
// @Summary Get all submissions for a user
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {array} models.TaskSubmissionDetails
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/submissions [get]
func GetUserSubmissions(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	currentUserID, exists := c.Get("userID")
	isAdmin, _ := CheckAdminRights(currentUserID.(int))
	if !exists || (userID != currentUserID.(int) && !isAdmin) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	submissions, err := Store.GetUserSubmissions(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve submissions: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, submissions)
}

// GetCourseStatistics
// @Summary Get statistics for a course
// @Tags Analytics
// @Produce json
// @Param course_id path int true "Course ID"
// @Success 200 {object} models.CourseStatistics
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /analytics/courses/{course_id}/statistics [get]
func GetCourseStatistics(c *gin.Context) {
	courseID, err := strconv.Atoi(c.Param("course_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	isAdmin, err := CheckAdminRights(c.GetInt("userID"))
	if err != nil || !isAdmin {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Only administrators can view course statistics"})
		return
	}

	stats, err := Store.GetCourseStatistics(courseID)
	if err != nil {
		if err.Error() == "course not found" {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Course not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve course statistics: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, stats)
}

// GetUserStatistics
// @Summary Get learning statistics for a user
// @Tags Analytics
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.UserStatistics
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /analytics/users/{user_id}/statistics [get]
func GetUserStatistics(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	currentUserID, exists := c.Get("userID")
	isAdmin, _ := CheckAdminRights(currentUserID.(int))
	if !exists || (userID != currentUserID.(int) && !isAdmin) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	stats, err := Store.GetUserStatistics(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve user statistics: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, stats)
}

// GetUserLearningPath
// @Summary Get personalized learning path for a user
// @Tags Progress
// @Produce json
// @Param user_id path int true "User ID"
// @Success 200 {object} models.LearningPath
// @Failure 400 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 500 {object} models.ErrorResponse
// @Router /progress/{user_id}/learning-path [get]
func GetUserLearningPath(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("user_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid user ID"})
		return
	}

	currentUserID, exists := c.Get("userID")
	if !exists || userID != currentUserID.(int) {
		c.JSON(http.StatusForbidden, models.ErrorResponse{Error: "Access denied"})
		return
	}

	learningPath, err := Store.GetUserLearningPath(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: "Failed to retrieve learning path: " + err.Error()})
		return
	}

	c.JSON(http.StatusOK, learningPath)
}

// CreateCourse
// @Summary Create a new course
// @Tags Courses
// @Produce json
// @Param title body string true "Title of the course"
// @Param description body string true "Description of the course"
// @Param difficulty_level body string true "Difficulty level of the course"
// @Param category body string true "Category of the course"
// @Success 201 {object} models.Course
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Router /api/courses [post]
func CreateCourse(c *gin.Context) {
	var course models.Course
	if err := c.BindJSON(&course); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request body"})
		return
	}

	course, err := Store.CreateCourse(course)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusCreated, course)
}

// UpdateCourse
// @Summary Update course information
// @Tags Courses
// @Produce json
// @Param id path int true "Course ID"
// @Param title body string true "Title of the course"
// @Param description body string true "Description of the course"
// @Param difficulty_level body string true "Difficulty level of the course"
// @Param category body string true "Category of the course"
// @Success 200 {object} models.Course
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Router /api/courses/{id} [put]
func UpdateCourse(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	var course models.Course
	if err := c.BindJSON(&course); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request body"})
		return
	}

	course, err = Store.UpdateCourse(id, course)
	if err != nil {
		if err.Error() == "course not found" {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Course not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, course)
}

// DeleteCourse
// @Summary Delete course
// @Tags Courses
// @Produce json
// @Param id path int true "Course ID"
// @Success 204 {object} models.SuccessResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Router /api/courses/{id} [delete]
func DeleteCourse(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	err = Store.DeleteCourse(id)
	if err != nil {
		if err.Error() == "course not found" {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Course not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusNoContent, models.SuccessResponse{Message: "Course deleted successfully"})
}

// CreateTask
// @Summary Create a new task
// @Tags Tasks
// @Produce json
// @Param course_id path int true "Course ID"
// @Param title body string true "Title of the task"
// @Param description body string true "Description of the task"
// @Param type body string true "Type of the task"
// @Param points body int true "Points for the task"
// @Param content body string true "Content of the task"
// @Success 201 {object} models.Task
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Router /api/courses/{course_id}/tasks [post]
func CreateTask(c *gin.Context) {
	var task models.Task
	if err := c.BindJSON(&task); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request body"})
		return
	}

	courseID, err := strconv.Atoi(c.Param("course_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}
	task.CourseID = courseID

	task, err = Store.CreateTask(courseID, task)
	if err != nil {
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusCreated, task)
}

// UpdateTask
// @Summary Update task information
// @Tags Tasks
// @Produce json
// @Param course_id path int true "Course ID"
// @Param task_id path int true "Task ID"
// @Param title body string true "Title of the task"
// @Param description body string true "Description of the task"
// @Param type body string true "Type of the task"
// @Param points body int true "Points for the task"
// @Param content body string true "Content of the task"
// @Success 200 {object} models.Task
// @Failure 400 {object} models.ErrorResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Router /api/courses/{course_id}/tasks/{task_id} [put]
func UpdateTask(c *gin.Context) {
	courseID, err := strconv.Atoi(c.Param("course_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	taskID, err := strconv.Atoi(c.Param("task_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid task ID"})
		return
	}

	var task models.Task
	if err := c.BindJSON(&task); err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid request body"})
		return
	}
	task.CourseID = courseID
	task.ID = taskID

	task, err = Store.UpdateTask(courseID, taskID, task)
	if err != nil {
		if err.Error() == "task not found" {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Task not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusOK, task)
}

// DeleteTask
// @Summary Delete task
// @Tags Tasks
// @Produce json
// @Param course_id path int true "Course ID"
// @Param task_id path int true "Task ID"
// @Success 204 {object} models.SuccessResponse
// @Failure 401 {object} models.ErrorResponse
// @Failure 403 {object} models.ErrorResponse
// @Failure 404 {object} models.ErrorResponse
// @Router /api/courses/{course_id}/tasks/{task_id} [delete]
func DeleteTask(c *gin.Context) {
	courseID, err := strconv.Atoi(c.Param("course_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid course ID"})
		return
	}

	taskID, err := strconv.Atoi(c.Param("task_id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, models.ErrorResponse{Error: "Invalid task ID"})
		return
	}

	err = Store.DeleteTask(courseID, taskID)
	if err != nil {
		if err.Error() == "task not found" {
			c.JSON(http.StatusNotFound, models.ErrorResponse{Error: "Task not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, models.ErrorResponse{Error: err.Error()})
		return
	}

	c.JSON(http.StatusNoContent, models.SuccessResponse{Message: "Task deleted successfully"})
}
