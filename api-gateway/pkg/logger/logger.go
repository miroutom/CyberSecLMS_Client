package logger

import (
	"fmt"
	"log"
	"os"
	"strings"
	"time"
)

type LogLevel int

const (
	LogLevelError LogLevel = iota
	LogLevelInfo
	LogLevelDebug
)

type Logger struct {
	infoLogger  *log.Logger
	errorLogger *log.Logger
	debugLogger *log.Logger
	level       LogLevel
}

func ParseLogLevel(level string) LogLevel {
	switch strings.ToUpper(level) {
	case "DEBUG":
		return LogLevelDebug
	case "INFO":
		return LogLevelInfo
	default:
		return LogLevelError
	}
}

func NewLogger(level string) *Logger {
	infoLogger := log.New(os.Stdout, "INFO: ", log.Ldate|log.Ltime|log.Lshortfile)
	errorLogger := log.New(os.Stderr, "ERROR: ", log.Ldate|log.Ltime|log.Lshortfile)
	debugLogger := log.New(os.Stdout, "DEBUG: ", log.Ldate|log.Ltime|log.Lshortfile)

	return &Logger{
		infoLogger:  infoLogger,
		errorLogger: errorLogger,
		debugLogger: debugLogger,
		level:       ParseLogLevel(level),
	}
}

func (l *Logger) Info(format string, args ...interface{}) {
	if l.level >= LogLevelInfo {
		l.log(l.infoLogger, format, args...)
	}
}

func (l *Logger) Error(format string, args ...interface{}) {
	l.log(l.errorLogger, format, args...)
}

func (l *Logger) Debug(format string, args ...interface{}) {
	if l.level >= LogLevelDebug {
		l.log(l.debugLogger, format, args...)
	}
}

func (l *Logger) Fatal(format string, args ...interface{}) {
	l.log(l.errorLogger, format, args...)
	os.Exit(1)
}

func (l *Logger) log(logger *log.Logger, format string, args ...interface{}) {
	message := format
	if len(args) > 0 {
		message = fmt.Sprintf(format, args...)
	}
	if err := logger.Output(2, message); err != nil {
		fmt.Fprintf(os.Stderr, "Failed to log message: %v\n", err)
	}
}

func (l *Logger) WithFields(fields map[string]interface{}) *Logger {
	return l
}

func (l *Logger) WithTime(t time.Time) *Logger {
	return l
}
