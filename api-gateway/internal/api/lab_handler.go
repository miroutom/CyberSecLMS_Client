package api

import (
	"lmsmodule/api-gateway/internal/models"
	"os"
	"path/filepath"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

type LabHandler struct {
	db *gorm.DB
}

func NewLabHandler(db *gorm.DB) *LabHandler {
	return &LabHandler{db: db}
}

func (h *LabHandler) InitLabs() error {
	labs := []struct {
		Title             string
		Description       string
		VulnerabilityType string
		Difficulty        string
		ContentFile       string
		SolutionFile      string
	}{
		{
			Title:             "SQL Injection",
			Description:       "Исправьте уязвимость SQL-инъекции",
			VulnerabilityType: "SQL_INJECTION",
			Difficulty:        "MEDIUM",
			ContentFile:       "labs/sql_injection/vulnerable.py",
			SolutionFile:      "solutions/sql_injection/fixed.py",
		},
	}

	for _, lab := range labs {
		content, err := os.ReadFile(filepath.Join("labs_data", lab.ContentFile))
		if err != nil {
			return err
		}

		solution, err := os.ReadFile(filepath.Join("labs_data", lab.SolutionFile))
		if err != nil {
			return err
		}

		if err := h.db.Create(&models.Lab{
			Title:             lab.Title,
			Description:       lab.Description,
			VulnerabilityType: lab.VulnerabilityType,
			Difficulty:        lab.Difficulty,
			Content:           string(content),
			Solution:          string(solution),
		}).Error; err != nil {
			return err
		}
	}
	return nil
}

func (h *LabHandler) GetLabs(c *gin.Context) {
	var labs []models.Lab
	if err := h.db.Find(&labs).Error; err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	c.JSON(200, labs)
}

func (h *LabHandler) GetLab(c *gin.Context) {
	var lab models.Lab
	if err := h.db.First(&lab, c.Param("id")).Error; err != nil {
		c.JSON(404, gin.H{"error": "Lab not found"})
		return
	}
	c.JSON(200, lab)
}
