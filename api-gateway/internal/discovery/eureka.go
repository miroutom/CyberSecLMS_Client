package discovery

import (
	"fmt"
	"strings"
	"sync"
	"time"

	"github.com/hudl/fargo"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type ServiceDiscovery struct {
	connection     *fargo.EurekaConnection
	services       map[string]string
	servicesMutex  sync.RWMutex
	fallbackConfig *utils.Config
	logger         *logger.Logger
}

func NewServiceDiscovery(config *utils.Config, logger *logger.Logger) (*ServiceDiscovery, error) {
	conn := fargo.NewConn(config.Eureka.URL)
	sd := &ServiceDiscovery{
		connection:     &conn,
		services:       make(map[string]string),
		fallbackConfig: config,
		logger:         logger,
	}

	go sd.refreshServices()

	return sd, nil
}

func (sd *ServiceDiscovery) refreshServices() {
	for {
		apps, err := sd.connection.GetApps()
		if err != nil {
			sd.logger.Error("Failed to get apps from Eureka: %v", err)
			time.Sleep(30 * time.Second)
			continue
		}

		sd.servicesMutex.Lock()
		for _, app := range apps {
			if len(app.Instances) > 0 {
				instance := app.Instances[0]
				url := fmt.Sprintf("http://%s:%d", instance.IPAddr, instance.Port)
				sd.services[strings.ToUpper(app.Name)] = url
				sd.logger.Debug("Discovered service %s at %s", app.Name, url)
			}
		}
		sd.servicesMutex.Unlock()

		time.Sleep(30 * time.Second)
	}
}

func (sd *ServiceDiscovery) GetServiceURL(serviceName string) string {
	serviceName = strings.ToUpper(serviceName)

	sd.servicesMutex.RLock()
	defer sd.servicesMutex.RUnlock()

	if url, ok := sd.services[serviceName]; ok {
		return url
	}

	switch serviceName {
	case "BACKEND-SERVICE", "AUTH-SERVICE":
		return sd.fallbackConfig.AuthService.URL
	case "COURSE-SERVICE":
		return sd.fallbackConfig.CourseService.URL
	case "CODE-EXECUTOR-SERVICE", "EXECUTOR-SVC":
		return sd.fallbackConfig.CodeExecutorService.URL
	default:
		sd.logger.Error("Unknown service requested: %s", serviceName)
		return ""
	}
}
