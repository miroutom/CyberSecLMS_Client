package utils

import (
	"os"
	"strconv"

	"gopkg.in/yaml.v2"
)

type Config struct {
	Port          int     `yaml:"port"`
	LogLevel      string  `yaml:"log_level"`
	AuthService   Service `yaml:"auth_service"`
	CourseService Service `yaml:"course_service"`
}

type CORSConfig struct {
	AllowOrigins     []string `yaml:"allow_origins"`
	AllowMethods     []string `yaml:"allow_methods"`
	AllowHeaders     []string `yaml:"allow_headers"`
	ExposeHeaders    []string `yaml:"expose_headers"`
	AllowCredentials bool     `yaml:"allow_credentials"`
	MaxAge           int      `yaml:"max_age"`
}

type Service struct {
	URL     string `yaml:"url"`
	Timeout int    `yaml:"timeout"`
}

func LoadConfig(path string) (*Config, error) {
	config := &Config{}

	if port := os.Getenv("PORT"); port != "" {
		p, err := strconv.Atoi(port)
		if err == nil {
			config.Port = p
		}
	}

	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	decoder := yaml.NewDecoder(file)
	if err := decoder.Decode(config); err != nil {
		return nil, err
	}

	return config, nil
}
