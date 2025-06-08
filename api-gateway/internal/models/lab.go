package models

import "time"

type Lab struct {
	ID                uint      `gorm:"primaryKey"`
	Title             string    `gorm:"size:255;not null"`
	Description       string    `gorm:"type:text;not null"`
	VulnerabilityType string    `gorm:"size:50;not null"`
	Difficulty        string    `gorm:"size:50;not null"`
	Content           string    `gorm:"type:text;not null"`
	Solution          string    `gorm:"type:text;not null"`
	LabArchiveURL     string    `gorm:"size:512"`
	CreatedAt         time.Time `gorm:"autoCreateTime"`
}

func (l *Lab) TableName() string {
	return "labs"
}
