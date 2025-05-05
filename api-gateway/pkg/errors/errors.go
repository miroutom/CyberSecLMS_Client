package errors

const (
	ErrInternal        = "internal_error"
	ErrNotFound        = "not_found"
	ErrBadRequest      = "bad_request"
	ErrUnauthorized    = "unauthorized"
	ErrForbidden       = "forbidden"
	ErrTooManyRequests = "too_many_requests"
)

type APIError struct {
	Type    string `json:"type"`
	Message string `json:"message"`
	Code    int    `json:"-"`
}

func (e *APIError) Error() string {
	return e.Message
}

func NewAPIError(errType, message string, code int) *APIError {
	return &APIError{
		Type:    errType,
		Message: message,
		Code:    code,
	}
}

func NewInternalError(message string) *APIError {
	return NewAPIError(ErrInternal, message, 500)
}

func NewNotFoundError(message string) *APIError {
	return NewAPIError(ErrNotFound, message, 404)
}

func NewBadRequestError(message string) *APIError {
	return NewAPIError(ErrBadRequest, message, 400)
}

func NewUnauthorizedError(message string) *APIError {
	return NewAPIError(ErrUnauthorized, message, 401)
}

func NewForbiddenError(message string) *APIError {
	return NewAPIError(ErrForbidden, message, 403)
}

func NewTooManyRequestsError(message string) *APIError {
	return NewAPIError(ErrTooManyRequests, message, 429)
}

func Wrap(err error, errType string, code int) *APIError {
	return &APIError{
		Type:    errType,
		Message: err.Error(),
		Code:    code,
	}
}

func WrapInternal(err error) *APIError {
	return Wrap(err, ErrInternal, 500)
}
