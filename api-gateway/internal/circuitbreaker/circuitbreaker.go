package circuitbreaker

import (
	"sync"
	"time"
)

type State int

const (
	Closed State = iota
	Open
	HalfOpen
)

type CircuitBreaker struct {
	mutex            sync.RWMutex
	state            State
	failureCount     int
	failureThreshold int
	resetTimeout     time.Duration
	lastStateChange  time.Time
	serviceName      string
}

func NewCircuitBreaker(serviceName string, failureThreshold int, resetTimeout time.Duration) *CircuitBreaker {
	return &CircuitBreaker{
		state:            Closed,
		failureCount:     0,
		failureThreshold: failureThreshold,
		resetTimeout:     resetTimeout,
		lastStateChange:  time.Now(),
		serviceName:      serviceName,
	}
}

func (cb *CircuitBreaker) IsAllowed() bool {
	cb.mutex.RLock()
	defer cb.mutex.RUnlock()

	switch cb.state {
	case Closed:
		return true
	case Open:
		if time.Since(cb.lastStateChange) > cb.resetTimeout {
			cb.mutex.RUnlock()
			cb.mutex.Lock()
			cb.state = HalfOpen
			cb.lastStateChange = time.Now()
			cb.mutex.Unlock()
			cb.mutex.RLock()
			return true
		}
		return false
	case HalfOpen:
		return true
	default:
		return true
	}
}

func (cb *CircuitBreaker) Success() {
	cb.mutex.Lock()
	defer cb.mutex.Unlock()

	if cb.state == HalfOpen {
		cb.state = Closed
		cb.failureCount = 0
		cb.lastStateChange = time.Now()
	} else if cb.state == Closed {
		cb.failureCount = 0
	}
}

func (cb *CircuitBreaker) Failure() {
	cb.mutex.Lock()
	defer cb.mutex.Unlock()

	if cb.state == HalfOpen {
		cb.state = Open
		cb.lastStateChange = time.Now()
	} else if cb.state == Closed {
		cb.failureCount++
		if cb.failureCount >= cb.failureThreshold {
			cb.state = Open
			cb.lastStateChange = time.Now()
		}
	}
}

func (cb *CircuitBreaker) GetState() State {
	cb.mutex.RLock()
	defer cb.mutex.RUnlock()
	return cb.state
}

func (cb *CircuitBreaker) Reset() {
	cb.mutex.Lock()
	defer cb.mutex.Unlock()
	cb.state = Closed
	cb.failureCount = 0
	cb.lastStateChange = time.Now()
}
