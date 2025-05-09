package storage

import (
	"crypto/subtle"
	"database/sql"
	"errors"
	"fmt"
	"golang.org/x/crypto/bcrypt"
	"lmsmodule/backend-svc/models"
	"time"
)

// GetCourses возвращает список всех курсов из базы данных
func (s *DBStorage) GetCourses() ([]models.Course, error) {
	stmt, err := s.DB.Prepare("SELECT id, title, description FROM courses")
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	rows, err := stmt.Query()
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var courses []models.Course
	for rows.Next() {
		var course models.Course
		if err := rows.Scan(&course.ID, &course.Title, &course.Description); err != nil {
			return nil, err
		}
		courses = append(courses, course)
	}

	return courses, nil
}

// GetCourseByID возвращает курс по ID из базы данных
func (s *DBStorage) GetCourseByID(id int) (models.Course, error) {
	stmt, err := s.DB.Prepare("SELECT id, title, description FROM courses WHERE id = ?")
	if err != nil {
		return models.Course{}, err
	}
	defer stmt.Close()

	var course models.Course
	err = stmt.QueryRow(id).Scan(&course.ID, &course.Title, &course.Description)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.Course{}, errors.New("course not found")
		}
		return models.Course{}, err
	}

	return course, nil
}

// GetUserProgress возвращает прогресс пользователя из базы данных
func (s *DBStorage) GetUserProgress(userID int) (models.UserProgress, error) {
	stmt, err := s.DB.Prepare("SELECT assignment_id FROM user_progress WHERE user_id = ?")
	if err != nil {
		return models.UserProgress{}, err
	}
	defer stmt.Close()

	rows, err := stmt.Query(userID)
	if err != nil {
		return models.UserProgress{}, err
	}
	defer rows.Close()

	completed := make(map[int]bool)
	for rows.Next() {
		var assignmentID int
		if err := rows.Scan(&assignmentID); err != nil {
			return models.UserProgress{}, err
		}
		completed[assignmentID] = true
	}

	return models.UserProgress{
		UserID:    userID,
		Completed: completed,
	}, nil
}

// CompleteAssignment отмечает задание как выполненное в базе данных
func (s *DBStorage) CompleteAssignment(userID, assignmentID int) error {
	stmt, err := s.DB.Prepare(
		"INSERT INTO user_progress (user_id, assignment_id) VALUES (?, ?) " +
			"ON DUPLICATE KEY UPDATE assignment_id = VALUES(assignment_id)")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID, assignmentID)
	return err
}

// CreateUser создает нового пользователя в базе данных
func (s *DBStorage) CreateUser(user models.User) error {
	// Проверяем, не существует ли уже пользователь с таким именем/email
	checkStmt, err := s.DB.Prepare("SELECT EXISTS(SELECT 1 FROM users WHERE username = ? OR email = ?)")
	if err != nil {
		return err
	}
	defer checkStmt.Close()

	var exists bool
	err = checkStmt.QueryRow(user.Username, user.Email).Scan(&exists)
	if err != nil {
		return err
	}

	if exists {
		return errors.New("username or email already exists")
	}

	// Вставляем нового пользователя
	insertStmt, err := s.DB.Prepare(
		"INSERT INTO users (username, password_hash, email, full_name, totp_secret, is_2fa_enabled, is_active) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?)")
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
		true, // is_active
	)

	return err
}

// GetUserByUsername возвращает пользователя по имени пользователя из базы данных
func (s *DBStorage) GetUserByUsername(username string) (models.User, error) {
	stmt, err := s.DB.Prepare(
		"SELECT id, username, password_hash, email, full_name, totp_secret, is_2fa_enabled " +
			"FROM users WHERE username = ?")
	if err != nil {
		return models.User{}, err
	}
	defer stmt.Close()

	var user models.User
	err = stmt.QueryRow(username).Scan(
		&user.ID,
		&user.Username,
		&user.PasswordHash,
		&user.Email,
		&user.FullName,
		&user.TOTPSecret,
		&user.Is2FAEnabled,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.User{}, errors.New("user not found")
		}
		return models.User{}, err
	}

	return user, nil
}

// GetUserByID возвращает пользователя по ID из базы данных
func (s *DBStorage) GetUserByID(id int) (models.User, error) {
	stmt, err := s.DB.Prepare(
		"SELECT id, username, password_hash, email, full_name, totp_secret, is_2fa_enabled " +
			"FROM users WHERE id = ?")
	if err != nil {
		return models.User{}, err
	}
	defer stmt.Close()

	var user models.User
	err = stmt.QueryRow(id).Scan(
		&user.ID,
		&user.Username,
		&user.PasswordHash,
		&user.Email,
		&user.FullName,
		&user.TOTPSecret,
		&user.Is2FAEnabled,
	)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return models.User{}, errors.New("user not found")
		}
		return models.User{}, err
	}

	return user, nil
}

func (s *DBStorage) SaveOTPCode(userID int, code string) error {
	expiresAt := time.Now().Add(5 * time.Minute)

	stmt, err := s.DB.Prepare("UPDATE users SET otp_code = ?, otp_expires_at = ? WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(code, expiresAt, userID)
	return err
}

func (s *DBStorage) VerifyOTPCode(userID int, code string) (bool, error) {
	var storedCode string
	var expiresAt time.Time

	stmt, err := s.DB.Prepare("SELECT otp_code, otp_expires_at FROM users WHERE id = ?")
	if err != nil {
		return false, err
	}
	defer stmt.Close()

	err = stmt.QueryRow(userID).Scan(&storedCode, &expiresAt)

	if err != nil {
		if err == sql.ErrNoRows {
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
	stmt, err := s.DB.Prepare("UPDATE users SET otp_code = NULL, otp_expires_at = NULL WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID)
	return err
}

// GetUsersByRole возвращает список пользователей с определенной ролью (admin или не admin)
func (s *DBStorage) GetUsersByRole(isAdmin bool) ([]models.User, error) {
	stmt, err := s.DB.Prepare(`
		SELECT id, username, password_hash, email, full_name, totp_secret, 
			   is_2fa_enabled, is_admin, is_active, last_login 
		FROM users
		WHERE is_admin = ?
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
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&lastLogin,
		)
		if err != nil {
			return nil, err
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

// UpdateUserLastLogin обновляет время последнего входа пользователя в базе данных
func (s *DBStorage) UpdateUserLastLogin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET last_login = NOW() WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID)
	return err
}

// Enable2FA включает двухфакторную аутентификацию для пользователя в базе данных
func (s *DBStorage) Enable2FA(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_2fa_enabled = TRUE WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID)
	return err
}

// IsAdmin проверяет, является ли пользователь администратором, в базе данных
func (s *DBStorage) IsAdmin(userID int) (bool, error) {
	stmt, err := s.DB.Prepare("SELECT is_admin FROM users WHERE id = ?")
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

// GetAllUsers возвращает список всех пользователей из базы данных
func (s *DBStorage) GetAllUsers() ([]models.User, error) {
	stmt, err := s.DB.Prepare(`
		SELECT id, username, password_hash, email, full_name, totp_secret, 
			   is_2fa_enabled, is_admin, is_active, last_login 
		FROM users
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
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&lastLogin,
		)
		if err != nil {
			return nil, err
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

// UpdateUserProfile обновляет профиль пользователя в базе данных
func (s *DBStorage) UpdateUserProfile(userID int, data models.UpdateProfileRequest) error {
	tx, err := s.DB.Begin()
	if err != nil {
		return err
	}
	defer func() {
		if err != nil {
			rollbackErr := tx.Rollback()
			if rollbackErr != nil {
				err = fmt.Errorf("original error: %w, rollback error: %v", err, rollbackErr)
			}
		}
	}()

	if data.Email != "" {
		stmt, err := tx.Prepare("UPDATE users SET email = ? WHERE id = ?")
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
		stmt, err := tx.Prepare("UPDATE users SET full_name = ? WHERE id = ?")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(data.FullName, userID)
		if err != nil {
			return err
		}
	}

	if data.Password != "" {
		hashedPassword, err := bcrypt.GenerateFromPassword([]byte(data.Password), bcrypt.DefaultCost)
		if err != nil {
			return err
		}

		stmt, err := tx.Prepare("UPDATE users SET password_hash = ? WHERE id = ?")
		if err != nil {
			return err
		}
		defer stmt.Close()

		_, err = stmt.Exec(string(hashedPassword), userID)
		if err != nil {
			return err
		}
	}

	return tx.Commit()
}

// SearchUsers ищет пользователей по имени пользователя, email или полному имени
func (s *DBStorage) SearchUsers(query string) ([]models.User, error) {
	// Добавляем % для поиска подстроки
	searchQuery := "%" + query + "%"

	stmt, err := s.DB.Prepare(`
		SELECT id, username, password_hash, email, full_name, totp_secret, 
			   is_2fa_enabled, is_admin, is_active, last_login 
		FROM users
		WHERE username LIKE ? OR email LIKE ? OR full_name LIKE ?
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
		var lastLogin sql.NullTime

		err := rows.Scan(
			&user.ID,
			&user.Username,
			&user.PasswordHash,
			&user.Email,
			&user.FullName,
			&user.TOTPSecret,
			&user.Is2FAEnabled,
			&user.IsAdmin,
			&user.IsActive,
			&lastLogin,
		)
		if err != nil {
			return nil, err
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

// UpdateUserStatus обновляет статус пользователя (активен/неактивен)
func (s *DBStorage) UpdateUserStatus(userID int, isActive bool) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_active = ? WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(isActive, userID)
	return err
}

// PromoteToAdmin повышает пользователя до администратора
func (s *DBStorage) PromoteToAdmin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_admin = TRUE WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID)
	return err
}

// DemoteFromAdmin понижает пользователя с роли администратора
func (s *DBStorage) DemoteFromAdmin(userID int) error {
	stmt, err := s.DB.Prepare("UPDATE users SET is_admin = FALSE WHERE id = ?")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(userID)
	return err
}
