#!/bin/bash

echo "Starting Ecommerce Backend Services..."

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "Starting $service_name on port $port..."
    cd "$service_dir"
    
    # Check if Maven is available
    if command -v mvn &> /dev/null; then
        mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=$port" &
        echo "$service_name started with PID: $!"
    else
        echo "Maven not found. Please install Maven first."
        exit 1
    fi
    
    cd ..
    sleep 5
}

# Start services in order
echo "Starting Auth Service..."
start_service "Auth Service" "auth-service" "8081"

echo "Starting Tenant Service..."
start_service "Tenant Service" "tenant-service" "8082"

echo "Starting Product Service..."
start_service "Product Service" "product-service" "8083"

echo "Starting Order Service..."
start_service "Order Service" "order-service" "8084"

echo "All services started successfully!"
echo ""
echo "Service URLs:"
echo "Auth Service: http://localhost:8081"
echo "Tenant Service: http://localhost:8082"
echo "Product Service: http://localhost:8083"
echo "Order Service: http://localhost:8084"
echo ""
echo "Swagger Documentation:"
echo "Auth Service: http://localhost:8081/swagger-ui.html"
echo "Tenant Service: http://localhost:8082/swagger-ui.html"
echo "Product Service: http://localhost:8083/swagger-ui.html"
echo "Order Service: http://localhost:8084/swagger-ui.html"
echo ""
echo "Press Ctrl+C to stop all services"
echo ""

# Wait for user to stop
wait 