package api

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

type ScaleRequest struct {
	ServiceName string `json:"service_name" binding:"required"`
	Replicas    int    `json:"replicas" binding:"required"`
}

func (s *Server) setupScalingRoutes() {
	scaling := s.Router.Group("/api/admin/scaling")
	{
		scaling.GET("/services", s.getServicesHandler)
		scaling.GET("/services/:name", s.getServiceInstancesHandler)
		scaling.POST("/services/:name/scale", s.scaleServiceHandler)
	}
}

func (s *Server) getServicesHandler(c *gin.Context) {
	services := s.Discovery.GetAllServices()
	c.JSON(http.StatusOK, gin.H{
		"services": services,
	})
}

func (s *Server) getServiceInstancesHandler(c *gin.Context) {
	serviceName := c.Param("name")
	instances := s.Discovery.GetServiceInstances(serviceName)
	c.JSON(http.StatusOK, gin.H{
		"service":   serviceName,
		"instances": instances,
		"count":     len(instances),
	})
}

func (s *Server) scaleServiceHandler(c *gin.Context) {
	serviceName := c.Param("name")
	replicasStr := c.Query("replicas")
	replicas, err := strconv.Atoi(replicasStr)
	if err != nil || replicas < 1 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid replicas parameter"})
		return
	}

	currentInstances := s.Discovery.GetServiceInstances(serviceName)
	currentCount := len(currentInstances)

	if replicas > currentCount {
		c.JSON(http.StatusOK, gin.H{
			"message":           "Scaling request accepted",
			"current_instances": currentCount,
			"target_instances":  replicas,
			"status":            "in_progress",
			"note":              "Real implementation would trigger container orchestration",
		})
	} else if replicas < currentCount {
		c.JSON(http.StatusOK, gin.H{
			"message":           "Scale down request accepted",
			"current_instances": currentCount,
			"target_instances":  replicas,
			"status":            "in_progress",
			"note":              "Real implementation would trigger container orchestration",
		})
	} else {
		c.JSON(http.StatusOK, gin.H{
			"message":   "No scaling needed, current instance count matches requested",
			"instances": currentCount,
		})
	}
}
