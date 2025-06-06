package discovery

import (
	"fmt"
	"math/rand"
	"strings"
	"sync"
	"time"

	"github.com/hudl/fargo"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

type ServiceDiscovery struct {
	connection     *fargo.EurekaConnection
	services       map[string][]string
	servicesMutex  sync.RWMutex
	fallbackConfig *utils.Config
	logger         *logger.Logger
}

func NewServiceDiscovery(config *utils.Config, logger *logger.Logger) (*ServiceDiscovery, error) {
	conn := fargo.NewConn(config.Eureka.URL)
	sd := &ServiceDiscovery{
		connection:     &conn,
		services:       make(map[string][]string),
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
		for k := range sd.services {
			sd.services[k] = []string{}
		}

		for _, app := range apps {
			serviceName := strings.ToUpper(app.Name)

			for _, instance := range app.Instances {
				if instance.Status == fargo.UP {
					url := fmt.Sprintf("http://%s:%d", instance.IPAddr, instance.Port)
					sd.services[serviceName] = append(sd.services[serviceName], url)
					sd.logger.Debug("Discovered instance of %s at %s", serviceName, url)
				}
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

	if urls, ok := sd.services[serviceName]; ok && len(urls) > 0 {
		return urls[rand.Intn(len(urls))]
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

func (sd *ServiceDiscovery) GetServiceInstances(serviceName string) []string {
	serviceName = strings.ToUpper(serviceName)

	sd.servicesMutex.RLock()
	defer sd.servicesMutex.RUnlock()

	if urls, ok := sd.services[serviceName]; ok {
		return urls
	}

	return []string{}
}

func (sd *ServiceDiscovery) GetAllServices() map[string]int {
	sd.servicesMutex.RLock()
	defer sd.servicesMutex.RUnlock()

	result := make(map[string]int)
	for name, urls := range sd.services {
		result[name] = len(urls)
	}

	return result
}

func (sd *ServiceDiscovery) RegisterNewInstance(serviceName, ipAddr string, port int) error {
	instance := fargo.Instance{
		InstanceId:       fmt.Sprintf("%s:%s:%d", serviceName, ipAddr, port),
		HostName:         ipAddr,
		App:              strings.ToUpper(serviceName),
		IPAddr:           ipAddr,
		Port:             port,
		PortEnabled:      true,
		VipAddress:       serviceName,
		SecureVipAddress: serviceName,
		Status:           fargo.UP,
		DataCenterInfo:   fargo.DataCenterInfo{Name: fargo.MyOwn},
		HomePageUrl:      fmt.Sprintf("http://%s:%d/", ipAddr, port),
		StatusPageUrl:    fmt.Sprintf("http://%s:%d/health", ipAddr, port),
		HealthCheckUrl:   fmt.Sprintf("http://%s:%d/health", ipAddr, port),
		CountryId:        1,
	}

	return sd.connection.RegisterInstance(&instance)
}

func (sd *ServiceDiscovery) DeregisterInstance(serviceName, ipAddr string) error {
	instance := fargo.Instance{
		HostName: ipAddr,
		App:      strings.ToUpper(serviceName),
	}
	return sd.connection.DeregisterInstance(&instance)
}
