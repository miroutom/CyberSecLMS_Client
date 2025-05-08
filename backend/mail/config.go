package mail

import (
	"fmt"
	"html/template"
	"os"
	"path/filepath"
)

var (
	smtpHost     = "smtp.mail.ru"
	smtpPort     = "465"
	smtpUsername = "tristan.nik@mail.ru"
	smtpPassword = "xYCkbEGbm4Vf4WrrMX5H"
	smtpFrom     = "Cybersecurity Platform <tristan.nik@mail.ru>"

	emailTemplates map[string]*template.Template
)

func init() {
	loadEnvironmentVariables()
	loadEmailTemplates()
}

func loadEnvironmentVariables() {
	if host := os.Getenv("SMTP_HOST"); host != "" {
		smtpHost = host
	}
	if port := os.Getenv("SMTP_PORT"); port != "" {
		smtpPort = port
	}
	if username := os.Getenv("SMTP_USERNAME"); username != "" {
		smtpUsername = username
	}
	if password := os.Getenv("SMTP_PASSWORD"); password != "" {
		smtpPassword = password
	}
	if from := os.Getenv("SMTP_FROM"); from != "" {
		smtpFrom = from
	}
}

func loadEmailTemplates() {
	emailTemplates = make(map[string]*template.Template)

	rootDir, err := os.Getwd()
	if err != nil {
		fmt.Printf("Error getting working directory: %v\n", err)
		return
	}

	templatePath := filepath.Join(rootDir, "backend", "templates", "emails", "otp_email.html")
	tmpl, err := template.ParseFiles(templatePath)
	if err != nil {
		fmt.Printf("Error loading email template: %v\n", err)
		return
	}

	emailTemplates["otp_email"] = tmpl
}

func ReloadTemplates() error {
	rootDir, err := os.Getwd()
	if err != nil {
		return fmt.Errorf("error getting working directory: %v", err)
	}

	templatePath := filepath.Join(rootDir, "templates", "emails", "otp_email.html")
	tmpl, err := template.ParseFiles(templatePath)
	if err != nil {
		return fmt.Errorf("error loading email template: %v", err)
	}

	emailTemplates["otp_email"] = tmpl
	return nil
}
