package ut

import (
	"database/sql"
	"lmsmodule/backend-svc/handlers"
	"lmsmodule/backend-svc/storage"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestUseStorage(t *testing.T) {
	mockStore := new(storage.MockStorage)
	handlers.UseStorage(mockStore)

	assert.Equal(t, mockStore, handlers.Store)
}

func TestJWTSecret(t *testing.T) {
	originalSecret := handlers.JWTSecret
	defer func() {
		handlers.JWTSecret = originalSecret
	}()

	handlers.JWTSecret = "new_test_secret"
	assert.Equal(t, "new_test_secret", handlers.JWTSecret)
}

func TestDbVariable(t *testing.T) {
	assert.Nil(t, handlers.Db)

	db := &sql.DB{}
	handlers.Db = db

	assert.Equal(t, db, handlers.Db)
}
