package handlers

import (
	"github.com/gin-gonic/gin"
	"lmsmodule/backend-svc/models"
	"net/http"
	"strconv"
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

	// Проверка прав доступа - пользователь может отмечать только свои задания
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
