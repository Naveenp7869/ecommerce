# 🚨 Ecommerce Project Troubleshooting Guide

This guide addresses the specific issues found in your project and provides step-by-step solutions.

## ❌ Issues Found and Fixed

### 1. **Maven Not Installed**
**Problem**: `mvn : The term 'mvn' is not recognized`
**Solution**: Use the Maven wrapper (mvnw.cmd) instead of global Maven installation

### 2. **POM File Dependency Issues**
**Problem**: `'dependencies.dependency.version' for org.springframework.boot:spring-boot-starter-kafka:jar is missing`
**Solution**: Added missing versions to all Kafka dependencies in POM files

### 3. **Outdated Spring Boot Version**
**Problem**: Using Spring Boot 3.2.0 (outdated)
**Solution**: Updated to Spring Boot 3.5.4 (latest stable)

### 4. **Missing .gitignore File**
**Problem**: No .gitignore file to exclude build artifacts and sensitive files
**Solution**: Created comprehensive .gitignore file

### 5. **Windows Compatibility Issues**
**Problem**: Shell scripts don't work on Windows PowerShell
**Solution**: Created PowerShell script for starting services

## 🔧 How to Use the Fixed Project

### Prerequisites
1. **Java 17+** must be installed and JAVA_HOME set
2. **Node.js 18+** for frontend
3. **Docker Desktop** for infrastructure services

### Step 1: Start Infrastructure
```powershell
# Start databases and Kafka
docker-compose up -d
```

### Step 2: Build Backend (Using Maven Wrapper)
```powershell
cd backend
.\mvnw.cmd clean install
```

### Step 3: Start Backend Services
```powershell
# Use the PowerShell script (Windows)
.\start-services.ps1

# Or manually start each service:
cd auth-service
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
```

### Step 4: Start Frontend
```powershell
cd frontend
npm install
npm run dev
```

## 🚀 Quick Start Commands

### For Windows Users:
```powershell
# 1. Start infrastructure
docker-compose up -d

# 2. Build backend
cd backend
.\mvnw.cmd clean install

# 3. Start all services
.\start-services.ps1

# 4. Start frontend (in new terminal)
cd frontend
npm install
npm run dev
```

### For Linux/Mac Users:
```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Build backend
cd backend
./mvnw clean install

# 3. Start all services
./start-services.sh

# 4. Start frontend
cd frontend
npm install
npm run dev
```

## 🔍 Verification Steps

### Check Backend Services:
- Auth Service: http://localhost:8081/actuator/health
- Tenant Service: http://localhost:8082/actuator/health
- Product Service: http://localhost:8083/actuator/health
- Order Service: http://localhost:8084/actuator/health
- Notification Service: http://localhost:8085/actuator/health

### Check Frontend:
- Main App: http://localhost:3000

### Check Infrastructure:
- MySQL: localhost:3306
- MongoDB: localhost:27017
- Kafka: localhost:9092
- Redis: localhost:6379

## 🛠️ Common Issues and Solutions

### Issue: "JAVA_HOME is not defined"
**Solution**: Set JAVA_HOME environment variable
```powershell
# Windows
setx JAVA_HOME "C:\Program Files\Java\jdk-17"

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Issue: "Port already in use"
**Solution**: Check and kill processes using the port
```powershell
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8081
kill -9 <PID>
```

### Issue: "Database connection failed"
**Solution**: Ensure Docker containers are running
```powershell
docker ps
docker-compose logs mysql
docker-compose logs mongodb
```

### Issue: "Maven build fails"
**Solution**: Use Maven wrapper and check Java version
```powershell
java -version
.\mvnw.cmd clean install
```

## 📁 Project Structure After Fixes

```
Ecommerce/
├── .gitignore                    ✅ NEW - Excludes build artifacts
├── backend/
│   ├── mvnw.cmd                 ✅ NEW - Maven wrapper for Windows
│   ├── start-services.ps1       ✅ NEW - PowerShell service starter
│   ├── pom.xml                  ✅ FIXED - Updated dependencies
│   ├── auth-service/
│   │   └── pom.xml             ✅ FIXED - Added Kafka version
│   ├── tenant-service/
│   │   └── pom.xml             ✅ FIXED - Added Kafka version
│   ├── product-service/
│   │   └── pom.xml             ✅ FIXED - Added Kafka version
│   ├── order-service/
│   │   └── pom.xml             ✅ FIXED - Added Kafka version
│   └── notification-service/
│       └── pom.xml             ✅ FIXED - Added Kafka version
├── frontend/                     ✅ READY - No issues found
└── docker-compose.yml           ✅ READY - No issues found
```

## 🎯 Next Steps

1. **Install Java 17+** if not already installed
2. **Start Docker Desktop**
3. **Run the quick start commands above**
4. **Verify all services are running**
5. **Access the application at http://localhost:3000**

## 🆘 Still Having Issues?

If you encounter other problems:

1. **Check the logs**: Look at service console output for error messages
2. **Verify prerequisites**: Ensure Java, Node.js, and Docker are properly installed
3. **Check ports**: Make sure no other applications are using the required ports
4. **Database connectivity**: Verify MySQL and MongoDB containers are running
5. **Network issues**: Check if firewall is blocking connections

## 📞 Support

For additional help:
1. Check service logs for specific error messages
2. Verify all prerequisites are met
3. Ensure Docker containers are healthy
4. Check if ports are available

---

**Happy coding! 🎉**
