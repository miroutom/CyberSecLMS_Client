package models

type Request struct {
	Method      string
	Path        string
	QueryParams map[string]string
	Headers     map[string]string
	Body        []byte
}

type Response struct {
	StatusCode int
	Headers    map[string]string
	Body       []byte
}

type ServiceInfo struct {
	Name     string
	BaseURL  string
	IsActive bool
}
