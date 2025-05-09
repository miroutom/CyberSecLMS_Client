package mail

import (
	"bytes"
	"crypto/tls"
	"fmt"
	"net/smtp"
	"strings"
	"time"
)

type OTPEmailData struct {
	Code string
}

func SendOTPEmail(email, code string) error {
	template, ok := emailTemplates["otp_email"]
	if !ok {
		fmt.Printf("Email template not found, using fallback template\n")
		return sendOTPEmailFallback(email, code)
	}

	data := OTPEmailData{
		Code: code,
	}

	var bodyBuffer bytes.Buffer
	if err := template.Execute(&bodyBuffer, data); err != nil {
		fmt.Printf("Error executing email template: %v\n", err)
		return sendOTPEmailFallback(email, code)
	}

	auth := smtp.PlainAuth("", smtpUsername, smtpPassword, smtpHost)

	boundary := "boundary-" + strings.ReplaceAll(time.Now().String(), " ", "-")
	subject := "Your Verification Code"

	message := []byte(fmt.Sprintf("From: %s\r\n"+
		"To: %s\r\n"+
		"Subject: %s\r\n"+
		"MIME-Version: 1.0\r\n"+
		"Content-Type: multipart/alternative; boundary=%s; charset=UTF-8\r\n"+
		"\r\n"+
		"--%s\r\n"+
		"Content-Type: text/plain; charset=UTF-8\r\n"+
		"\r\n"+
		"Your verification code is: %s\r\nThis code is valid for 5 minutes.\r\n"+
		"\r\n"+
		"--%s\r\n"+
		"Content-Type: text/html; charset=UTF-8\r\n"+
		"\r\n"+
		"%s\r\n"+
		"\r\n"+
		"--%s--",
		smtpFrom, email, subject, boundary, boundary, code, boundary, bodyBuffer.String(), boundary))

	err := smtp.SendMail(smtpHost+":"+smtpPort, auth, smtpUsername, []string{email}, message)
	if err != nil {
		fmt.Printf("Error sending email: %v\n", err)
		fmt.Printf("OTP for %s: %s\n", email, code)
		return err
	}

	fmt.Printf("Email sent successfully to %s\n", email)
	return nil
}

func sendOTPEmailFallback(email, code string) error {
	tlsConfig := &tls.Config{
		ServerName: smtpHost,
	}

	conn, err := tls.Dial("tcp", smtpHost+":"+smtpPort, tlsConfig)
	if err != nil {
		fmt.Printf("SSL connection error: %v\n", err)
		fmt.Printf("OTP for %s: %s\n", email, code)
		return err
	}

	client, err := smtp.NewClient(conn, smtpHost)
	if err != nil {
		fmt.Printf("Error creating SMTP client: %v\n", err)
		return err
	}
	defer client.Close()

	auth := smtp.PlainAuth("", smtpUsername, smtpPassword, smtpHost)
	if err = client.Auth(auth); err != nil {
		fmt.Printf("Authentication error: %v\n", err)
		fmt.Printf("OTP for %s: %s\n", email, code)
		return err
	}

	if err = client.Mail(smtpUsername); err != nil {
		return err
	}

	if err = client.Rcpt(email); err != nil {
		return err
	}

	w, err := client.Data()
	if err != nil {
		return err
	}

	plainText := fmt.Sprintf("Your verification code is: %s\nThis code is valid for 5 minutes.", code)

	message := []byte(fmt.Sprintf("From: %s\r\n"+
		"To: %s\r\n"+
		"Subject: Your Verification Code\r\n"+
		"Content-Type: text/plain; charset=UTF-8\r\n"+
		"\r\n"+
		"%s",
		smtpFrom, email, plainText))

	_, err = w.Write(message)
	if err != nil {
		return err
	}

	err = w.Close()
	if err != nil {
		return err
	}

	fmt.Printf("Fallback email sent successfully to %s\n", email)
	return nil
}
