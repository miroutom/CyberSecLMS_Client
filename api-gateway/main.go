package main

import (
	"flag"
	"log"

	"lmsmodule/api-gateway/internal/api"
	"lmsmodule/api-gateway/internal/utils"
	"lmsmodule/api-gateway/pkg/logger"
)

func main() {
	configPath := flag.String("config", "./api-gateway/configs/config.yaml", "Path to configuration file")
	flag.Parse()

	config, err := utils.LoadConfig(*configPath)
	if err != nil {
		log.Fatalf("Failed to load configuration: %v", err)
	}

	logger := logger.NewLogger(config.LogLevel)

	server := api.NewServer(config, logger)
	logger.Info("Starting API Gateway on port %d", config.Port)
	if err := server.Run(); err != nil {
		logger.Fatal("Failed to start server: %v", err)
	}
}
