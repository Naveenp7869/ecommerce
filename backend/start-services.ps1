# PowerShell script to start all backend services
# This script starts all microservices in separate PowerShell windows

Write-Host "Starting Ecommerce Backend Services..." -ForegroundColor Green

# Function to start a service
function Start-Service {
    param(
        [string]$ServiceName,
        [string]$ServicePath,
        [string]$Port
    )
    
    Write-Host "Starting $ServiceName on port $Port..." -ForegroundColor Yellow
    
    $command = "cd '$ServicePath'; .\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=$Port'"
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal
}

# Get the current directory
$backendDir = Get-Location

# Start each service
Start-Service -ServiceName "Auth Service" -ServicePath "$backendDir\auth-service" -Port "8081"
Start-Sleep -Seconds 2

Start-Service -ServiceName "Tenant Service" -ServicePath "$backendDir\tenant-service" -Port "8082"
Start-Sleep -Seconds 2

Start-Service -ServiceName "Product Service" -ServicePath "$backendDir\product-service" -Port "8083"
Start-Sleep -Seconds 2

Start-Service -ServiceName "Order Service" -ServicePath "$backendDir\order-service" -Port "8084"
Start-Sleep -Seconds 2

Start-Service -ServiceName "Notification Service" -ServicePath "$backendDir\notification-service" -Port "8085"
Start-Sleep -Seconds 2

Start-Service -ServiceName "Discovery Service" -ServicePath "$backendDir\discovery-service" -Port "8761"

Write-Host "All services are starting..." -ForegroundColor Green
Write-Host "Check the following URLs to verify services are running:" -ForegroundColor Cyan
Write-Host "Auth Service: http://localhost:8081/actuator/health" -ForegroundColor White
Write-Host "Tenant Service: http://localhost:8082/actuator/health" -ForegroundColor White
Write-Host "Product Service: http://localhost:8083/actuator/health" -ForegroundColor White
Write-Host "Order Service: http://localhost:8084/actuator/health" -ForegroundColor White
Write-Host "Notification Service: http://localhost:8085/actuator/health" -ForegroundColor White
Write-Host "Discovery Service: http://localhost:8761/actuator/health" -ForegroundColor White

Write-Host "Press any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
