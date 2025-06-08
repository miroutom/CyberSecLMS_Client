package storage

import (
	"crypto/subtle"
	"database/sql"
	"errors"
	"fmt"
	"lmsmodule/backend-svc/models"
	"time"
)

// ****** МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ ******

func (s *DBStorage) CreateUser(user models.User) error {
	checkStmt, err := s.DB.Prepare("SELECT id, is_deleted FROM users WHERE username = ? OR email = ?")
	if err != nil {
		return err
	}
	defer checkStmt.Close()

	var userID int
	var isDeleted bool
	err = checkStmt.QueryRow(user.Username, user.Email).Scan(&userID, &isDeleted)

	if err != nil {
		if !errors.Is(err, sql.ErrNoRows) {
			return err
		}
		insertStmt, err := s.DB.Prepare(
			"INSERT INTO users (username, password_hash, email, full_name, totp_secret, is_2fa_enabled, is_active, is_teacher, is_deleted) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
		if err != nil {
			return err
		}
		defer insertStmt.Close()

		_, err = insertStmt.Exec(
			user.Username,
			user.PasswordHash,
			user.Email,
			user.FullName,
			user.TOTPSecret,
			user.Is2FAEnabled,
			true,
			user.IsTeacher,
			false,
		)
		return err
	}

	if !isDeleted {
		return errors.New("username or email already exists")
	}

	updateStmt, err := s.DB.Prepare(
		"UPDATE users SET password_hash = ?, email = ?, full_name = ?, totp_secret = ?, is_2fa_enabled = ?, is_active = ?, is_teacher = ?, is_deleted = ? WHERE id = ?")
	if err != nil {
		return err
	}
	defer updateStmt.Close()

	_, err = updateStmt.Exec(
		user.PasswordHash,
		user.Email,
		user.FullName,
		user.TOTPSecret,
		user.Is2FAEnabled,
		true,
		user.IsTeacher,
		false, // not deleted
		userID,
	)
	return err
}

func (s *DBStorage) GetUserByUsername(username string) (models.User, error) {
	stmt, err := s.DB.Prepare(
		"SELECT id, username, password_hash, email, full_name, totp_secret, is_2fa_enabled, is_admin, is_active, is_teacher, is_deleted, last_login " +
			"FROM users WHERE username = ?")
	if err != nil {
		return models.User{}, err
	}
	defer stmt.Close()

	var user models.User
	var lastLogin sql.NullTime
	err = stmt.QueryRow(username).Scan(
		&user.ID,
		&user.Username,
		&user.PasswordHash,
		&user.Email,
		&user.FullName,
		&user.TOTPSecret,
		&user.Is2FAEnabled,
		&user.IsAdmin,
		&user.IsActive,
		&user.IsTeacher,
		&user.IsDeleted,
		&lastLogin,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.User{}, errors.New("user not found")
		}
		return models.User{}, err
	}

	if user.IsDeleted {
		return models.User{}, errors.New("user not found")
	}

	if lastLogin.Valid {
		user.LastLogin = lastLogin.Time
	}

	return user, nil
}

func (s *DBStorage) GetUserByID(userID int) (models.User, error) {
	stmt, err := s.DB.Prepare(`
        SELECT id, username, password_hash, email, full_name, profile_image, totp_secret, 
               is_2fa_enabled, is_admin, is_active, is_teacher, is_deleted, last_login
        FROM users
        WHERE id = ?
    `)
	if err != nil {
		return models.User{}, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	var user models.User
	var profileImage sql.NullString
	var lastLogin sql.NullTime

	err = stmt.QueryRow(userID).Scan(
		&user.ID,
		&user.Username,
		&user.PasswordHash,
		&user.Email,
		&user.FullName,
		&profileImage,
		&user.TOTPSecret,
		&user.Is2FAEnabled,
		&user.IsAdmin,
		&user.IsActive,
		&user.IsTeacher,
		&user.IsDeleted,
		&lastLogin,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.User{}, fmt.Errorf("user not found")
		}
		return models.User{}, fmt.Errorf("query user: %w", err)
	}

	if user.IsDeleted {
		return models.User{}, fmt.Errorf("user not found")
	}

	if profileImage.Valid && profileImage.String != "" {
		user.ProfileImage = profileImage.String
	}

	if lastLogin.Valid {
		user.LastLogin = lastLogin.Time
	}

	courses, err := s.GetCourses()
	if err != nil {
		return models.User{}, fmt.Errorf("get courses: %w", err)
	}

	userProgress, err := s.GetUserProgress(userID)
	if err != nil {
		return models.User{}, fmt.Errorf("get user progress: %w", err)
	}

	for _, course := range courses {
		courseDetails, err := s.GetCourseByID(course.ID)
		if err != nil {
			return models.User{}, fmt.Errorf("get course details: %w", err)
		}

		courseProgress := models.CourseProgress{
			ID:                courseDetails.ID,
			VulnerabilityType: courseDetails.VulnerabilityType,
			Description:       courseDetails.Description,
			TasksCount:        courseDetails.TasksCount,
		}

		for _, task := range courseDetails.Tasks {
			if completed, exists := userProgress.Completed[task.ID]; exists && completed {
				courseProgress.CompletedTasks++
			}
		}

		if courseProgress.TasksCount > 0 {
			courseProgress.Progress = float64(courseProgress.CompletedTasks) / float64(courseProgress.TasksCount) * 100
		}

		user.Courses = append(user.Courses, courseProgress)
		user.CompletedTasks += courseProgress.CompletedTasks
		user.TotalTasks += courseProgress.TasksCount
	}

	if user.TotalTasks > 0 {
		user.Progress = float64(user.CompletedTasks) / float64(user.TotalTasks) * 100
	}

	return user, nil
}

func (s *DBStorage) SaveOTPCode(userID int, code string) error {
	expiresAt := time.Now().Add(5 * time.Minute)

	stmt, err := s.DB.Prepare("UPDATE users SET otp_code = ?, otp_expires_at = ? WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(code, expiresAt, userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) VerifyOTPCode(userID int, code string) (bool, error) {
	var storedCode string
	var expiresAt time.Time

	stmt, err := s.DB.Prepare("SELECT otp_code, otp_expires_at FROM users WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return false, err
	}
	defer stmt.Close()

	err = stmt.QueryRow(userID).Scan(&storedCode, &expiresAt)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return false, nil
		}
		return false, err
	}

	if storedCode == "" {
		return false, nil
	}

	if time.Now().After(expiresAt) {
		return false, nil
	}

	return subtle.ConstantTimeCompare([]byte(code), []byte(storedCode)) == 1, nil
}

func (s *DBStorage) ClearOTPCode(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET otp_code = NULL, otp_expires_at = NULL WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) UpdateUserLastLogin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET last_login = NOW() WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) Enable2FA(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_2fa_enabled = TRUE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) UpdateUserProfile(userID int, data models.UpdateProfileRequest) error {
	tx, err := s.DB.Begin()
	if err != nil {
		return err
	}

	commit := false
	defer func() {
		if !commit {
			rbErr := tx.Rollback()
			if rbErr != nil && err == nil {
				err = rbErr
			}
		}
	}()

	var exists bool
	err = tx.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE id = ? AND is_deleted = FALSE)", userID).Scan(&exists)
	if err != nil {
		return err
	}

	if !exists {
		return errors.New("user not found or already deleted")
	}

	if data.Email != "" {
		stmt, err := tx.Prepare("UPDATE users SET email = ? WHERE id = ? AND is_deleted = FALSE")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(data.Email, userID)
		if err != nil {
			return err
		}
	}

	if data.FullName != "" {
		stmt, err := tx.Prepare("UPDATE users SET full_name = ? WHERE id = ? AND is_deleted = FALSE")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(data.FullName, userID)
		if err != nil {
			return err
		}
	}

	if data.Username != "" {
		stmt, err := tx.Prepare("UPDATE users SET username = ? WHERE id = ? AND is_deleted = FALSE")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(data.Username, userID)
		if err != nil {
			return err
		}
	}

	err = tx.Commit()
	if err == nil {
		commit = true
	}
	return err
}

func (s *DBStorage) UpdatePassword(userID int, data models.UpdateProfileRequest) error {
	tx, err := s.DB.Begin()
	if err != nil {
		return err
	}

	commit := false
	defer func() {
		if !commit {
			rbErr := tx.Rollback()
			if rbErr != nil && err == nil {
				err = rbErr
			}
		}
	}()

	var exists bool
	err = tx.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE id = ? AND is_deleted = FALSE)", userID).Scan(&exists)
	if err != nil {
		return err
	}

	if !exists {
		return errors.New("user not found or already deleted")
	}

	if data.Password != "" {
		stmt, err := tx.Prepare("UPDATE users SET password_hash = ? WHERE id = ? AND is_deleted = FALSE")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(data.Username, userID)
		if err != nil {
			return err
		}
	}

	err = tx.Commit()
	if err == nil {
		commit = true
	}
	return err
}

func (s *DBStorage) UpdateUserProfileImage(userID int, imageURL string) error {
	stmt, err := s.DB.Prepare("UPDATE users SET profile_image = ? WHERE id = ?")
	if err != nil {
		return fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	result, err := stmt.Exec(imageURL, userID)
	if err != nil {
		return fmt.Errorf("execute statement: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("get rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return fmt.Errorf("user not found")
	}

	return nil
}

func (s *DBStorage) DeleteUser(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return fmt.Errorf("execute statement: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("get rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return fmt.Errorf("user not found or already deleted")
	}

	return nil
}

// ****** АДМИНИСТРАТИВНЫЕ МЕТОДЫ ******

func (s *DBStorage) IsAdmin(userID int) (bool, error) {
	stmt, err := s.DB.Prepare("SELECT is_admin FROM users WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return false, err
	}
	defer stmt.Close()

	var isAdmin bool
	err = stmt.QueryRow(userID).Scan(&isAdmin)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return false, errors.New("user not found")
		}
		return false, err
	}

	return isAdmin, nil
}

func (s *DBStorage) GetAllUsers() ([]models.User, error) {
	stmt, err := s.DB.Prepare(`
        SELECT id, username, password_hash, email, full_name, profile_image, totp_secret, 
               is_2fa_enabled, is_admin, is_active, is_teacher, last_login 
        FROM users
        WHERE is_deleted = FALSE
    `)
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	rows, err := stmt.Query()
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []models.User
	for rows.Next() {
		var user models.User
		var profileImage sql.NullString
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&profileImage,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&user.IsTeacher,
			&lastLogin,
		)
		if err != nil {
			return nil, err
		}

		if profileImage.Valid {
			user.ProfileImage = profileImage.String
		}

		if lastLogin.Valid {
			user.LastLogin = lastLogin.Time
		}

		users = append(users, user)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return users, nil
}

func (s *DBStorage) GetUsersByRole(isAdmin bool) ([]models.User, error) {
	stmt, err := s.DB.Prepare(`
        SELECT id, username, password_hash, email, full_name, profile_image, totp_secret, 
               is_2fa_enabled, is_admin, is_active, is_teacher, last_login 
        FROM users
        WHERE is_admin = ? AND is_deleted = FALSE
    `)
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	rows, err := stmt.Query(isAdmin)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []models.User
	for rows.Next() {
		var user models.User
		var profileImage sql.NullString
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&profileImage,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&user.IsTeacher,
			&lastLogin,
		)
		if err != nil {
			return nil, err
		}

		if profileImage.Valid {
			user.ProfileImage = profileImage.String
		}

		if lastLogin.Valid {
			user.LastLogin = lastLogin.Time
		}

		users = append(users, user)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return users, nil
}

func (s *DBStorage) SearchUsers(query string) ([]models.User, error) {
	searchQuery := "%" + query + "%"

	stmt, err := s.DB.Prepare(`
        SELECT id, username, password_hash, email, full_name, profile_image, totp_secret, 
               is_2fa_enabled, is_admin, is_active, is_teacher, last_login 
        FROM users
        WHERE (username LIKE ? OR email LIKE ? OR full_name LIKE ?) AND is_deleted = FALSE
    `)
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	rows, err := stmt.Query(searchQuery, searchQuery, searchQuery)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []models.User
	for rows.Next() {
		var user models.User
		var profileImage sql.NullString
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&profileImage,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&user.IsTeacher,
			&lastLogin,
		)
		if err != nil {
			return nil, err
		}

		if profileImage.Valid {
			user.ProfileImage = profileImage.String
		}

		if lastLogin.Valid {
			user.LastLogin = lastLogin.Time
		}

		users = append(users, user)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return users, nil
}

func (s *DBStorage) UpdateUserStatus(userID int, isActive bool) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_active = ? WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(isActive, userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) PromoteToAdmin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_admin = TRUE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) DemoteFromAdmin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_admin = FALSE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

// ****** МЕТОДЫ ДЛЯ РАБОТЫ С КУРСАМИ ******

var (
	ErrCourseNotFound = errors.New("course not found")
)

func (s *DBStorage) GetCourses() ([]models.Course, error) {
	stmt, err := s.DB.Prepare(`
		SELECT c.id, c.vulnerability_type, 
			   COUNT(t.id) as tasks_count, c.description
		FROM courses c
		LEFT JOIN tasks t ON c.id = t.course_id
		GROUP BY c.id
	`)
	if err != nil {
		return nil, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	rows, err := stmt.Query()
	if err != nil {
		return nil, fmt.Errorf("execute query: %w", err)
	}
	defer rows.Close()

	var courses []models.Course
	for rows.Next() {
		var course models.Course
		if err := rows.Scan(
			&course.ID,
			&course.VulnerabilityType,
			&course.TasksCount,
			&course.Description,
		); err != nil {
			return nil, fmt.Errorf("scan row: %w", err)
		}
		courses = append(courses, course)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate rows: %w", err)
	}

	return courses, nil
}

func (s *DBStorage) GetCourseByID(id int) (models.Course, error) {
	tx, err := s.DB.Begin()
	if err != nil {
		return models.Course{}, fmt.Errorf("begin transaction: %w", err)
	}

	var txErr error
	defer func() {
		if txErr != nil {
			_ = tx.Rollback()
		} else {
			_ = tx.Commit()
		}
	}()

	courseStmt, err := tx.Prepare(`
		SELECT c.id, c.vulnerability_type, 
			   COUNT(t.id) as tasks_count, c.description
		FROM courses c
		LEFT JOIN tasks t ON c.id = t.course_id
		WHERE c.id = ?
		GROUP BY c.id
	`)
	if err != nil {
		txErr = err
		return models.Course{}, fmt.Errorf("prepare course statement: %w", err)
	}
	defer courseStmt.Close()

	var course models.Course
	err = courseStmt.QueryRow(id).Scan(
		&course.ID,
		&course.VulnerabilityType,
		&course.TasksCount,
		&course.Description,
	)

	if err != nil {
		txErr = err
		if errors.Is(err, sql.ErrNoRows) {
			return models.Course{}, ErrCourseNotFound
		}
		return models.Course{}, fmt.Errorf("query course: %w", err)
	}

	tasksStmt, err := tx.Prepare(`
		SELECT id, course_id, title, description, difficulty, task_order
		FROM tasks
		WHERE course_id = ?
		ORDER BY task_order
	`)
	if err != nil {
		txErr = err
		return models.Course{}, fmt.Errorf("prepare tasks statement: %w", err)
	}
	defer tasksStmt.Close()

	tasksRows, err := tasksStmt.Query(id)
	if err != nil {
		txErr = err
		return models.Course{}, fmt.Errorf("query tasks: %w", err)
	}
	defer tasksRows.Close()

	var tasks []models.Task
	for tasksRows.Next() {
		var task models.Task
		if err := tasksRows.Scan(
			&task.ID,
			&task.CourseID,
			&task.Title,
			&task.Description,
			&task.Difficulty,
			&task.Order,
		); err != nil {
			txErr = err
			return models.Course{}, fmt.Errorf("scan task: %w", err)
		}
		tasks = append(tasks, task)
	}

	if err := tasksRows.Err(); err != nil {
		txErr = err
		return models.Course{}, fmt.Errorf("iterate tasks: %w", err)
	}

	course.Tasks = tasks
	return course, nil
}

// ****** МЕТОДЫ ДЛЯ РАБОТЫ С ЗАДАНИЯМИ И ПРОГРЕССОМ ******

var (
	ErrTaskNotFound = errors.New("task not found")
)

func (s *DBStorage) GetUserProgress(userID int) (models.UserProgress, error) {
	var exists bool
	err := s.DB.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE id = ? AND is_deleted = FALSE)", userID).Scan(&exists)
	if err != nil {
		return models.UserProgress{}, fmt.Errorf("check user: %w", err)
	}

	if !exists {
		return models.UserProgress{}, fmt.Errorf("user not found or deleted")
	}

	stmt, err := s.DB.Prepare("SELECT task_id FROM user_progress WHERE user_id = ?")
	if err != nil {
		return models.UserProgress{}, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	rows, err := stmt.Query(userID)
	if err != nil {
		return models.UserProgress{}, fmt.Errorf("execute query: %w", err)
	}
	defer rows.Close()

	completed := make(map[int]bool)
	for rows.Next() {
		var taskID int
		if err := rows.Scan(&taskID); err != nil {
			return models.UserProgress{}, fmt.Errorf("scan row: %w", err)
		}
		completed[taskID] = true
	}

	if err := rows.Err(); err != nil {
		return models.UserProgress{}, fmt.Errorf("iterate rows: %w", err)
	}

	return models.UserProgress{
		UserID:    userID,
		Completed: completed,
	}, nil
}

func (s *DBStorage) GetTaskByID(courseID, taskID int) (models.Task, error) {
	stmt, err := s.DB.Prepare(`
		SELECT 
			id, course_id, title, description, difficulty, task_order, points
		FROM tasks
		WHERE id = ? AND course_id = ?
	`)
	if err != nil {
		return models.Task{}, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	var task models.Task
	err = stmt.QueryRow(taskID, courseID).Scan(
		&task.ID,
		&task.CourseID,
		&task.Title,
		&task.Description,
		&task.Difficulty,
		&task.Order,
		&task.Points,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.Task{}, errors.New("task not found")
		}
		return models.Task{}, fmt.Errorf("query task: %w", err)
	}

	return task, nil
}

func (s *DBStorage) CompleteTask(userID, taskID int) error {
	var userExists bool
	err := s.DB.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE id = ? AND is_deleted = FALSE)", userID).Scan(&userExists)
	if err != nil {
		return fmt.Errorf("check user: %w", err)
	}

	if !userExists {
		return fmt.Errorf("user not found or deleted")
	}

	var taskExists bool
	err = s.DB.QueryRow("SELECT EXISTS(SELECT 1 FROM tasks WHERE id = ?)", taskID).Scan(&taskExists)
	if err != nil {
		return fmt.Errorf("check task existence: %w", err)
	}

	if !taskExists {
		return ErrTaskNotFound
	}

	stmt, err := s.DB.Prepare(
		"INSERT INTO user_progress (user_id, task_id) VALUES (?, ?) " +
			"ON DUPLICATE KEY UPDATE task_id = VALUES(task_id)")
	if err != nil {
		return fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	if _, err := stmt.Exec(userID, taskID); err != nil {
		return fmt.Errorf("execute statement: %w", err)
	}

	return nil
}

func (s *DBStorage) SubmitTaskAnswer(submission models.TaskSubmission) (models.TaskSubmissionResponse, error) {
	err := s.CompleteTask(submission.UserID, submission.TaskID)
	if err != nil {
		return models.TaskSubmissionResponse{}, fmt.Errorf("complete task: %w", err)
	}

	return models.TaskSubmissionResponse{
		SubmissionID: 0,
		TaskID:       submission.TaskID,
		Status:       "completed",
		SubmittedAt:  submission.SubmittedAt,
		Message:      "Task marked as completed",
	}, nil
}

func (s *DBStorage) GetUserSubmissions(userID int) ([]models.TaskSubmissionDetails, error) {
	stmt, err := s.DB.Prepare(`
		SELECT 
			t.id, t.title, t.course_id, c.vulnerability_type, up.created_at
		FROM user_progress up
		JOIN tasks t ON up.task_id = t.id
		JOIN courses c ON t.course_id = c.id
		WHERE up.user_id = ?
		ORDER BY up.created_at DESC
	`)
	if err != nil {
		return nil, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	rows, err := stmt.Query(userID)
	if err != nil {
		return nil, fmt.Errorf("execute query: %w", err)
	}
	defer rows.Close()

	var submissions []models.TaskSubmissionDetails
	for rows.Next() {
		var submission models.TaskSubmissionDetails
		var submittedAt time.Time

		if err := rows.Scan(
			&submission.TaskID,
			&submission.TaskTitle,
			&submission.CourseID,
			&submission.CourseName,
			&submittedAt,
		); err != nil {
			return nil, fmt.Errorf("scan row: %w", err)
		}

		submission.SubmissionID = submission.TaskID
		submission.SubmittedAt = submittedAt
		submission.Status = "completed"

		submissions = append(submissions, submission)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate rows: %w", err)
	}

	return submissions, nil
}

func (s *DBStorage) GetCourseStatistics(courseID int) (models.CourseStatistics, error) {
	var stats models.CourseStatistics
	stats.CourseID = courseID

	err := s.DB.QueryRow("SELECT vulnerability_type FROM courses WHERE id = ?", courseID).Scan(&stats.CourseName)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return stats, errors.New("course not found")
		}
		return stats, fmt.Errorf("get course name: %w", err)
	}

	err = s.DB.QueryRow(`
		SELECT 
			COUNT(DISTINCT up.user_id) as enrolled_students,
			COUNT(DISTINCT CASE WHEN (
				SELECT COUNT(*) FROM tasks WHERE course_id = ?
			) = (
				SELECT COUNT(*) FROM user_progress up2 
				JOIN tasks t2 ON up2.task_id = t2.id 
				WHERE t2.course_id = ? AND up2.user_id = up.user_id
			) THEN up.user_id ELSE NULL END) as completed_students
		FROM user_progress up
		JOIN tasks t ON up.task_id = t.id
		WHERE t.course_id = ?
	`, courseID, courseID, courseID).Scan(
		&stats.EnrolledStudents,
		&stats.CompletedStudents,
	)
	if err != nil {
		return stats, fmt.Errorf("get course stats: %w", err)
	}

	if stats.EnrolledStudents > 0 {
		stats.AverageCompletion = float64(stats.CompletedStudents) / float64(stats.EnrolledStudents) * 100
	}

	taskStmt, err := s.DB.Prepare(`
		SELECT 
			t.id, t.title,
			COUNT(DISTINCT up.user_id) as completed_by
		FROM tasks t
		LEFT JOIN user_progress up ON t.id = up.task_id
		WHERE t.course_id = ?
		GROUP BY t.id
	`)
	if err != nil {
		return stats, fmt.Errorf("prepare task stats statement: %w", err)
	}
	defer taskStmt.Close()

	taskRows, err := taskStmt.Query(courseID)
	if err != nil {
		return stats, fmt.Errorf("execute task stats query: %w", err)
	}
	defer taskRows.Close()

	stats.TaskCompletionRates = []struct {
		TaskID       int     `json:"task_id"`
		TaskTitle    string  `json:"task_title"`
		CompletedBy  int     `json:"completed_by"`
		SuccessRate  float64 `json:"success_rate"`
		AverageScore float64 `json:"average_score"`
	}{}

	for taskRows.Next() {
		var taskStat struct {
			TaskID       int     `json:"task_id"`
			TaskTitle    string  `json:"task_title"`
			CompletedBy  int     `json:"completed_by"`
			SuccessRate  float64 `json:"success_rate"`
			AverageScore float64 `json:"average_score"`
		}
		if err := taskRows.Scan(
			&taskStat.TaskID,
			&taskStat.TaskTitle,
			&taskStat.CompletedBy,
		); err != nil {
			return stats, fmt.Errorf("scan task stats row: %w", err)
		}

		if stats.EnrolledStudents > 0 {
			taskStat.SuccessRate = float64(taskStat.CompletedBy) / float64(stats.EnrolledStudents) * 100
		}
		stats.TaskCompletionRates = append(stats.TaskCompletionRates, taskStat)
	}

	if err := taskRows.Err(); err != nil {
		return stats, fmt.Errorf("iterate task stats rows: %w", err)
	}

	studentStmt, err := s.DB.Prepare(`
		SELECT 
			u.id, u.username,
			COUNT(DISTINCT CASE WHEN t.course_id = ? THEN up.task_id ELSE NULL END) as completed_tasks,
			(SELECT COUNT(*) FROM tasks WHERE course_id = ?) as total_tasks,
			MAX(up.created_at) as last_activity
		FROM users u
		JOIN user_progress up ON u.id = up.user_id
		JOIN tasks t ON up.task_id = t.id
		WHERE u.is_deleted = 0
		GROUP BY u.id
		HAVING completed_tasks > 0
		ORDER BY completed_tasks DESC
	`)
	if err != nil {
		return stats, fmt.Errorf("prepare student progress statement: %w", err)
	}
	defer studentStmt.Close()

	studentRows, err := studentStmt.Query(courseID, courseID)
	if err != nil {
		return stats, fmt.Errorf("execute student progress query: %w", err)
	}
	defer studentRows.Close()

	stats.StudentsProgress = []struct {
		UserID            int     `json:"user_id"`
		Username          string  `json:"username"`
		CompletionPercent float64 `json:"completion_percentage"`
		AverageScore      float64 `json:"average_score"`
		LastActivity      string  `json:"last_activity"`
	}{}

	for studentRows.Next() {
		var studentProgress struct {
			UserID            int     `json:"user_id"`
			Username          string  `json:"username"`
			CompletionPercent float64 `json:"completion_percentage"`
			AverageScore      float64 `json:"average_score"`
			LastActivity      string  `json:"last_activity"`
		}
		var completedTasks, totalTasks int
		var lastActivity time.Time

		if err := studentRows.Scan(
			&studentProgress.UserID,
			&studentProgress.Username,
			&completedTasks,
			&totalTasks,
			&lastActivity,
		); err != nil {
			return stats, fmt.Errorf("scan student progress row: %w", err)
		}

		if totalTasks > 0 {
			studentProgress.CompletionPercent = float64(completedTasks) / float64(totalTasks) * 100
		}
		studentProgress.LastActivity = lastActivity.Format("2006-01-02 15:04:05")

		stats.StudentsProgress = append(stats.StudentsProgress, studentProgress)
	}

	if err := studentRows.Err(); err != nil {
		return stats, fmt.Errorf("iterate student progress rows: %w", err)
	}

	return stats, nil
}

func (s *DBStorage) GetUserStatistics(userID int) (models.UserStatistics, error) {
	var stats models.UserStatistics
	stats.UserID = userID

	var createdAt, lastActive time.Time
	err := s.DB.QueryRow(`
		SELECT 
			created_at,
			IFNULL((SELECT MAX(created_at) FROM user_progress WHERE user_id = ?), created_at) as last_active
		FROM users
		WHERE id = ? AND is_deleted = 0
	`, userID, userID).Scan(&createdAt, &lastActive)

	if err != nil {
		return stats, fmt.Errorf("get user base info: %w", err)
	}

	stats.JoinedDate = createdAt
	stats.LastActive = lastActive

	err = s.DB.QueryRow(`
		SELECT
			(SELECT COUNT(DISTINCT course_id) FROM tasks t JOIN user_progress up ON t.id = up.task_id WHERE up.user_id = ?) as total_courses,
			(SELECT COUNT(DISTINCT course_id) FROM (
				SELECT t.course_id, COUNT(t.id) as total_tasks, COUNT(up.task_id) as completed_tasks
				FROM tasks t
				LEFT JOIN user_progress up ON t.id = up.task_id AND up.user_id = ?
				GROUP BY t.course_id
				HAVING total_tasks = completed_tasks AND total_tasks > 0
			) as completed_courses) as completed_courses,
			(SELECT COUNT(*) FROM tasks) as total_tasks,
			(SELECT COUNT(*) FROM user_progress WHERE user_id = ?) as completed_tasks
	`, userID, userID, userID).Scan(
		&stats.TotalCourses,
		&stats.CompletedCourses,
		&stats.TotalTasks,
		&stats.CompletedTasks,
	)
	if err != nil {
		return stats, fmt.Errorf("get user stats: %w", err)
	}

	courseStmt, err := s.DB.Prepare(`
		SELECT 
			c.id, c.vulnerability_type,
			COUNT(DISTINCT up.task_id) as completed_tasks,
			(SELECT COUNT(*) FROM tasks WHERE course_id = c.id) as total_tasks,
			MAX(up.created_at) as last_activity
		FROM courses c
		JOIN tasks t ON c.id = t.course_id
		JOIN user_progress up ON t.id = up.task_id
		WHERE up.user_id = ?
		GROUP BY c.id, c.vulnerability_type
		ORDER BY last_activity DESC
	`)
	if err != nil {
		return stats, fmt.Errorf("prepare course progress statement: %w", err)
	}
	defer courseStmt.Close()

	courseRows, err := courseStmt.Query(userID)
	if err != nil {
		return stats, fmt.Errorf("execute course progress query: %w", err)
	}
	defer courseRows.Close()

	stats.CoursesProgress = []struct {
		CourseID          int     `json:"course_id"`
		CourseName        string  `json:"course_name"`
		CompletionPercent float64 `json:"completion_percentage"`
		AverageScore      float64 `json:"average_score"`
		LastActivity      string  `json:"last_activity"`
	}{}

	for courseRows.Next() {
		var courseProgress struct {
			CourseID          int     `json:"course_id"`
			CourseName        string  `json:"course_name"`
			CompletionPercent float64 `json:"completion_percentage"`
			AverageScore      float64 `json:"average_score"`
			LastActivity      string  `json:"last_activity"`
		}
		var completedTasks, totalTasks int
		var lastActivity time.Time

		if err := courseRows.Scan(
			&courseProgress.CourseID,
			&courseProgress.CourseName,
			&completedTasks,
			&totalTasks,
			&lastActivity,
		); err != nil {
			return stats, fmt.Errorf("scan course progress row: %w", err)
		}

		if totalTasks > 0 {
			courseProgress.CompletionPercent = float64(completedTasks) / float64(totalTasks) * 100
		}
		courseProgress.LastActivity = lastActivity.Format("2006-01-02 15:04:05")

		stats.CoursesProgress = append(stats.CoursesProgress, courseProgress)
	}

	if err := courseRows.Err(); err != nil {
		return stats, fmt.Errorf("iterate course progress rows: %w", err)
	}

	return stats, nil
}

func (s *DBStorage) GetLeaderboard(courseID int, limit int) ([]models.LeaderboardEntry, error) {
	courseFilter := ""
	params := []interface{}{}

	if courseID > 0 {
		courseFilter = "AND t.course_id = ?"
		params = append(params, courseID)
	}

	query := fmt.Sprintf(`
		SELECT 
			ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT up.task_id) DESC) as position,
			u.id, u.username,
			COUNT(DISTINCT up.task_id) * 10 as points,
			COUNT(DISTINCT up.task_id) as completed_tasks
		FROM users u
		JOIN user_progress up ON u.id = up.user_id
		JOIN tasks t ON up.task_id = t.id
		WHERE u.is_deleted = 0 %s
		GROUP BY u.id
		ORDER BY points DESC
		LIMIT ?
	`, courseFilter)

	params = append(params, limit)

	stmt, err := s.DB.Prepare(query)
	if err != nil {
		return nil, fmt.Errorf("prepare statement: %w", err)
	}
	defer stmt.Close()

	rows, err := stmt.Query(params...)
	if err != nil {
		return nil, fmt.Errorf("execute query: %w", err)
	}
	defer rows.Close()

	var leaderboard []models.LeaderboardEntry
	for rows.Next() {
		var entry models.LeaderboardEntry
		if err := rows.Scan(
			&entry.Position,
			&entry.UserID,
			&entry.Username,
			&entry.Points,
			&entry.Completed,
		); err != nil {
			return nil, fmt.Errorf("scan row: %w", err)
		}
		leaderboard = append(leaderboard, entry)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate rows: %w", err)
	}

	return leaderboard, nil
}

func (s *DBStorage) GetUserLearningPath(userID int) (models.LearningPath, error) {
	var path models.LearningPath
	path.UserID = userID
	path.GeneratedAt = time.Now()

	recStmt, err := s.DB.Prepare(`
		SELECT 
			c.id, c.vulnerability_type,
			CASE 
				WHEN NOT EXISTS (
					SELECT 1 FROM user_progress up 
					JOIN tasks t ON up.task_id = t.id 
					WHERE up.user_id = ? AND t.course_id = c.id
				) THEN 3
				WHEN (
					SELECT COUNT(*) FROM tasks WHERE course_id = c.id
				) > (
					SELECT COUNT(*) FROM user_progress up 
					JOIN tasks t ON up.task_id = t.id 
					WHERE up.user_id = ? AND t.course_id = c.id
				) THEN 2
				ELSE 1
			END as priority,
			CASE 
				WHEN NOT EXISTS (
					SELECT 1 FROM user_progress up 
					JOIN tasks t ON up.task_id = t.id 
					WHERE up.user_id = ? AND t.course_id = c.id
				) THEN 'New recommended course'
				ELSE 'Continue your progress'
			END as reason
		FROM courses c
		ORDER BY priority DESC, c.id
		LIMIT 3
	`)
	if err != nil {
		return path, fmt.Errorf("prepare recommendations statement: %w", err)
	}
	defer recStmt.Close()

	recRows, err := recStmt.Query(userID, userID, userID)
	if err != nil {
		return path, fmt.Errorf("execute recommendations query: %w", err)
	}
	defer recRows.Close()

	path.Recommendations = []struct {
		CourseID      int    `json:"course_id"`
		CourseName    string `json:"course_name"`
		Priority      int    `json:"priority"`
		Reason        string `json:"reason"`
		EstimatedTime string `json:"estimated_time"`
	}{}

	for recRows.Next() {
		var rec struct {
			CourseID      int    `json:"course_id"`
			CourseName    string `json:"course_name"`
			Priority      int    `json:"priority"`
			Reason        string `json:"reason"`
			EstimatedTime string `json:"estimated_time"`
		}
		if err := recRows.Scan(
			&rec.CourseID,
			&rec.CourseName,
			&rec.Priority,
			&rec.Reason,
		); err != nil {
			return path, fmt.Errorf("scan recommendation row: %w", err)
		}

		rec.EstimatedTime = "Based on course size"
		path.Recommendations = append(path.Recommendations, rec)
	}

	taskStmt, err := s.DB.Prepare(`
		SELECT 
			t.id, t.title, t.course_id, c.vulnerability_type,
			ROW_NUMBER() OVER (ORDER BY t.task_order) as priority
		FROM tasks t
		JOIN courses c ON t.course_id = c.id
		WHERE NOT EXISTS (
			SELECT 1 FROM user_progress up
			WHERE up.user_id = ? AND up.task_id = t.id
		)
		ORDER BY c.id, t.task_order
		LIMIT 5
	`)
	if err != nil {
		return path, fmt.Errorf("prepare next tasks statement: %w", err)
	}
	defer taskStmt.Close()

	taskRows, err := taskStmt.Query(userID)
	if err != nil {
		return path, fmt.Errorf("execute next tasks query: %w", err)
	}
	defer taskRows.Close()

	path.NextTasks = []struct {
		TaskID     int    `json:"task_id"`
		TaskTitle  string `json:"task_title"`
		CourseID   int    `json:"course_id"`
		CourseName string `json:"course_name"`
		Priority   int    `json:"priority"`
		DueDate    string `json:"due_date,omitempty"`
	}{}

	for taskRows.Next() {
		var task struct {
			TaskID     int    `json:"task_id"`
			TaskTitle  string `json:"task_title"`
			CourseID   int    `json:"course_id"`
			CourseName string `json:"course_name"`
			Priority   int    `json:"priority"`
			DueDate    string `json:"due_date,omitempty"`
		}
		if err := taskRows.Scan(
			&task.TaskID,
			&task.TaskTitle,
			&task.CourseID,
			&task.CourseName,
			&task.Priority,
		); err != nil {
			return path, fmt.Errorf("scan task row: %w", err)
		}

		path.NextTasks = append(path.NextTasks, task)
	}

	return path, nil
}

// ****** МЕТОДЫ ДЛЯ ПРЕПОДОВАТЕЛЯ ******

func (s *DBStorage) IsTeacher(userID int) (bool, error) {
	stmt, err := s.DB.Prepare("SELECT is_teacher FROM users WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return false, err
	}
	defer stmt.Close()

	var isTeacher bool
	err = stmt.QueryRow(userID).Scan(&isTeacher)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return false, errors.New("user not found")
		}
		return false, err
	}

	return isTeacher, nil
}

func (s *DBStorage) PromoteToTeacher(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_teacher = TRUE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) DemoteFromTeacher(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_teacher = FALSE WHERE id = ? AND is_deleted = FALSE")
	if err != nil {
		return err
	}
	defer stmt.Close()

	result, err := stmt.Exec(userID)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return errors.New("user not found or already deleted")
	}

	return nil
}

func (s *DBStorage) CreateCourse(course models.Course) (models.Course, error) {
	insertStmt, err := s.DB.Prepare(
		"INSERT INTO courses (vulnerability_type, description) VALUES (?, ?)")
	if err != nil {
		return models.Course{}, err
	}
	defer insertStmt.Close()

	result, err := insertStmt.Exec(course.VulnerabilityType, course.Description)
	if err != nil {
		return models.Course{}, err
	}

	id, err := result.LastInsertId()
	if err != nil {
		return models.Course{}, err
	}

	course.ID = int(id)
	return course, nil
}

func (s *DBStorage) UpdateCourse(id int, course models.Course) (models.Course, error) {
	updateStmt, err := s.DB.Prepare(
		"UPDATE courses SET vulnerability_type = ?, description = ? WHERE id = ?")
	if err != nil {
		return models.Course{}, err
	}
	defer updateStmt.Close()

	_, err = updateStmt.Exec(course.VulnerabilityType, course.Description, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.Course{}, ErrCourseNotFound
		}
		return models.Course{}, err
	}

	course.ID = id
	return course, nil
}

func (s *DBStorage) DeleteCourse(id int) error {
	deleteStmt, err := s.DB.Prepare("DELETE FROM courses WHERE id = ?")
	if err != nil {
		return err
	}
	defer deleteStmt.Close()

	_, err = deleteStmt.Exec(id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return ErrCourseNotFound
		}
		return err
	}

	return nil
}

func (s *DBStorage) CreateTask(courseID int, task models.Task) (models.Task, error) {
	insertStmt, err := s.DB.Prepare(
		"INSERT INTO tasks (course_id, title, description, difficulty, task_order) " +
			"VALUES (?, ?, ?, ?, ?)")
	if err != nil {
		return models.Task{}, err
	}
	defer insertStmt.Close()

	result, err := insertStmt.Exec(courseID, task.Title, task.Description, task.Difficulty, task.Order)
	if err != nil {
		return models.Task{}, err
	}

	id, err := result.LastInsertId()
	if err != nil {
		return models.Task{}, err
	}

	task.ID = int(id)
	return task, nil
}

func (s *DBStorage) UpdateTask(courseID, taskID int, task models.Task) (models.Task, error) {
	updateStmt, err := s.DB.Prepare(
		"UPDATE tasks SET title = ?, description = ?, difficulty = ?, task_order = ? " +
			"WHERE course_id = ? AND id = ?")
	if err != nil {
		return models.Task{}, err
	}
	defer updateStmt.Close()

	_, err = updateStmt.Exec(task.Title, task.Description, task.Difficulty, task.Order, courseID, taskID)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.Task{}, ErrTaskNotFound
		}
		return models.Task{}, err
	}

	task.CourseID = courseID
	task.ID = taskID
	return task, nil
}

func (s *DBStorage) DeleteTask(courseID, taskID int) error {
	deleteStmt, err := s.DB.Prepare("DELETE FROM tasks WHERE course_id = ? AND id = ?")
	if err != nil {
		return err
	}
	defer deleteStmt.Close()

	_, err = deleteStmt.Exec(courseID, taskID)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return ErrTaskNotFound
		}
		return err
	}

	return nil
}
