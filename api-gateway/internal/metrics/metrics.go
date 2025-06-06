package metrics

import (
	"sync"
	"time"
)

type ServiceMetrics struct {
	mutex           sync.RWMutex
	requestCounts   map[string]int
	responseTimes   map[string][]time.Duration
	errorCounts     map[string]int
	reportingPeriod time.Duration
	lastResetTime   time.Time
}

func NewServiceMetrics() *ServiceMetrics {
	sm := &ServiceMetrics{
		requestCounts:   make(map[string]int),
		responseTimes:   make(map[string][]time.Duration),
		errorCounts:     make(map[string]int),
		reportingPeriod: 1 * time.Minute,
		lastResetTime:   time.Now(),
	}
	go sm.periodicReset()
	return sm
}

func (sm *ServiceMetrics) RecordRequest(serviceName string) {
	sm.mutex.Lock()
	defer sm.mutex.Unlock()

	sm.requestCounts[serviceName]++
}

func (sm *ServiceMetrics) RecordResponseTime(serviceName string, duration time.Duration) {
	sm.mutex.Lock()
	defer sm.mutex.Unlock()

	sm.responseTimes[serviceName] = append(sm.responseTimes[serviceName], duration)
}

func (sm *ServiceMetrics) RecordError(serviceName string) {
	sm.mutex.Lock()
	defer sm.mutex.Unlock()

	sm.errorCounts[serviceName]++
}

func (sm *ServiceMetrics) GetRequestCount(serviceName string) int {
	sm.mutex.RLock()
	defer sm.mutex.RUnlock()

	return sm.requestCounts[serviceName]
}

func (sm *ServiceMetrics) GetAverageResponseTime(serviceName string) time.Duration {
	sm.mutex.RLock()
	defer sm.mutex.RUnlock()

	times := sm.responseTimes[serviceName]
	if len(times) == 0 {
		return 0
	}

	var total time.Duration
	for _, t := range times {
		total += t
	}

	return total / time.Duration(len(times))
}

func (sm *ServiceMetrics) GetErrorRate(serviceName string) float64 {
	sm.mutex.RLock()
	defer sm.mutex.RUnlock()

	requests := sm.requestCounts[serviceName]
	if requests == 0 {
		return 0
	}

	errors := sm.errorCounts[serviceName]
	return float64(errors) / float64(requests)
}

func (sm *ServiceMetrics) periodicReset() {
	for {
		time.Sleep(sm.reportingPeriod)

		sm.mutex.Lock()
		sm.requestCounts = make(map[string]int)
		sm.responseTimes = make(map[string][]time.Duration)
		sm.errorCounts = make(map[string]int)
		sm.lastResetTime = time.Now()
		sm.mutex.Unlock()
	}
}
