package api

import (
	"bytes"
	"encoding/json"
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
	"lmsmodule/api-gateway/internal/models"
	"mime/multipart"
	"net/http"
)

type SubmissionHandler struct {
	db *gorm.DB
}

func NewSubmissionHandler(db *gorm.DB) *SubmissionHandler {
	return &SubmissionHandler{db: db}
}

type SubmissionRequest struct {
	LabID  int    `json:"lab_id" binding:"required"`
	UserID int    `json:"user_id" binding:"required"`
	Code   string `json:"code" binding:"required"`
}

func (h *SubmissionHandler) SubmitSolution(c *gin.Context) {
	var req SubmissionRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(400, gin.H{"error": err.Error()})
		return
	}

	var lab models.Lab
	if err := h.db.First(&lab, req.LabID).Error; err != nil {
		c.JSON(404, gin.H{"error": "Lab not found"})
		return
	}

	body := &bytes.Buffer{}
	writer := multipart.NewWriter(body)

	codePart, err := writer.CreateFormField("code")
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to create form field: " + err.Error()})
		return
	}

	_, err = codePart.Write([]byte(req.Code))
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to write code: " + err.Error()})
		return
	}

	testsPart, err := writer.CreateFormFile("tests", "tests.py")
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to create form file: " + err.Error()})
		return
	}

	_, err = testsPart.Write([]byte(lab.Solution))
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to write tests: " + err.Error()})
		return
	}

	err = writer.Close()
	if err != nil {
		c.JSON(500, gin.H{"error": "Failed to close writer: " + err.Error()})
		return
	}

	resp, err := http.Post(
		"http://executor-svc:5000/execute_pytest",
		writer.FormDataContentType(),
		body,
	)
	if err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}
	defer resp.Body.Close()

	var result map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		c.JSON(500, gin.H{"error": err.Error()})
		return
	}

	c.JSON(resp.StatusCode, result)
}
