package utils

import (
	"os"
	"strconv"

	"gopkg.in/yaml.v2"
)

type Config struct {
	Port                int     `yaml:"port"`
	LogLevel            string  `yaml:"log_level"`
	AuthService         Service `yaml:"auth_service"`
	CourseService       Service `yaml:"course_service"`
	CodeExecutorService Service `yaml:"code_executor_service"`
	Eureka              Eureka  `yaml:"eureka"`
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

type Eureka struct {
	URL        string `yaml:"url"`
	AppName    string `yaml:"app_name"`
	InstanceIP string `yaml:"instance_ip"`
}

func LoadConfig(path string) (*Config, error) {
	config := &Config{}

	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	decoder := yaml.NewDecoder(file)
	if err := decoder.Decode(config); err != nil {
		return nil, err
	}

	if port := os.Getenv("PORT"); port != "" {
		p, err := strconv.Atoi(port)
		if err == nil {
			config.Port = p
		}
	}

	if eurekaURL := os.Getenv("EUREKA_URL"); eurekaURL != "" {
		config.Eureka.URL = eurekaURL
	}

	if appName := os.Getenv("APP_NAME"); appName != "" {
		config.Eureka.AppName = appName
	}

	if instanceIP := os.Getenv("INSTANCE_IP"); instanceIP != "" {
		config.Eureka.InstanceIP = instanceIP
	}

	return config, nil
}
