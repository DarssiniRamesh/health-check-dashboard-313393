#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    export $(grep -v '^#' .env | xargs)
fi

# Verify critical DB env vars are set
if [ -z "$SPRING_DATASOURCE_URL" ]; then
    echo "ERROR: SPRING_DATASOURCE_URL is not set"
    exit 1
fi

echo "Starting backend_api with Neon DB connection..."
echo "Database URL: $SPRING_DATASOURCE_URL"
echo "Database User: $SPRING_DATASOURCE_USERNAME"

# Start the Spring Boot application
exec ./gradlew bootRun
