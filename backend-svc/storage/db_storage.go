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
