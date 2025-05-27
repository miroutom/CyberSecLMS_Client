package handlers

import (
	"database/sql"
	"lmsmodule/backend-svc/storage"
)

// Глобальные переменные
var (
	Db        *sql.DB
	Store     storage.Storage
	JWTSecret = "your_strong_secret_here"
)

// UseStorage устанавливает хранилище для обработчиков
func UseStorage(s storage.Storage) {
	Store = s
}

// CheckAdminRights проверяет, имеет ли пользователь права администратора
func CheckAdminRights(userID int) (bool, error) {
	return Store.IsAdmin(userID)
}
