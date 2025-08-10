# 🚀 Ecommerce Application Setup Guide

This guide will help you set up and run the complete multi-tenant ecommerce application with Material-UI frontend and Kafka integration.

## 📋 Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17+**
   ```bash
   # Check Java version
   java -version
   # Should show version 17 or higher
   ```

2. **Apache Maven 3.6+**
   ```bash
   # Check Maven version
   mvn -version
   ```

3. **Node.js 18+ and npm**
   ```bash
   # Check Node.js version
   node --version
   # Check npm version
   npm --version
   ```

4. **MySQL 8.0+**
   - Download from [MySQL Official Site](https://dev.mysql.com/downloads/mysql/)
   - Or use Docker: `docker run --name mysql-db -e MYSQL_ROOT_PASSWORD=password -d -p 3306:3306 mysql:8.0`

5. **MongoDB 5.0+**
   - Download from [MongoDB Official Site](https://www.mongodb.com/try/download/community)
   - Or use Docker: `docker run --name mongodb -d -p 27017:27017 mongo:5.0`

6. **Apache Kafka (Optional for real-time features)**
   - Download from [Apache Kafka](https://kafka.apache.org/downloads)
   - Or use Docker Compose (see below)

## 🗄️ Database Setup

### MySQL Setup (for Auth, Tenant, Order services)

1. **Create databases:**
   ```sql
   CREATE DATABASE ecommerce_auth;
   CREATE DATABASE ecommerce_tenant;
   CREATE DATABASE ecommerce_order;
   
   -- Create user (optional)
   CREATE USER 'ecommerce_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON ecommerce_auth.* TO 'ecommerce_user'@'localhost';
   GRANT ALL PRIVILEGES ON ecommerce_tenant.* TO 'ecommerce_user'@'localhost';
   GRANT ALL PRIVILEGES ON ecommerce_order.* TO 'ecommerce_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

### MongoDB Setup (for Product service)

1. **Create database:**
   ```bash
   # Connect to MongoDB
   mongosh
   
   # Create database and collection
   use ecommerce_product
   db.createCollection("products")
   db.createCollection("categories")
   ```

## 🔧 Backend Setup

### 1. Navigate to Backend Directory
```bash
cd backend
```

### 2. Build All Services
```bash
# Build parent project and all modules
mvn clean install
```

### 3. Start Services

#### Option A: Using the provided script (Linux/Mac)
```bash
# Make script executable
chmod +x start-services.sh

# Start all services
./start-services.sh
```

#### Option B: Manual startup (Windows/All platforms)

**Terminal 1 - Auth Service:**
```bash
cd auth-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
```

**Terminal 2 - Tenant Service:**
```bash
cd tenant-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8082"
```

**Terminal 3 - Product Service:**
```bash
cd product-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8083"
```

**Terminal 4 - Order Service:**
```bash
cd order-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8084"
```

**Terminal 5 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8085"
```

**Terminal 6 - Discovery Service (Optional):**
```bash
cd discovery-service
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8761"
```

### 4. Verify Backend Services

Check that all services are running:
- Auth Service: http://localhost:8081/actuator/health
- Tenant Service: http://localhost:8082/actuator/health
- Product Service: http://localhost:8083/actuator/health
- Order Service: http://localhost:8084/actuator/health
- Notification Service: http://localhost:8085/actuator/health

## 🎨 Frontend Setup

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Create Environment File
```bash
# Create .env file in frontend directory
cat > .env << EOF
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=http://localhost:8080/ws
VITE_ENV=development
EOF
```

### 4. Start Development Server
```bash
npm run dev
```

The frontend will be available at: http://localhost:3000

## 🔄 Kafka Setup (Optional - for Real-time Features)

### Option A: Docker Compose (Recommended)

1. **Create docker-compose.yml:**
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    depends_on:
      - kafka
    ports:
      - "8090:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
```

2. **Start Kafka:**
```bash
docker-compose up -d
```

3. **Create Kafka Topics:**
```bash
# Connect to Kafka container
docker exec -it <kafka-container-name> bash

# Create topics
kafka-topics --create --topic order-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic product-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic tenant-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic notifications --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Option B: Local Installation

1. **Download and extract Kafka**
2. **Start Zookeeper:**
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
3. **Start Kafka:**
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

## 🌐 Access the Application

### Frontend Application
- **Main Application:** http://localhost:3000
- **Login Page:** http://localhost:3000/login
- **Register Page:** http://localhost:3000/register

### Backend APIs
- **Auth Service:** http://localhost:8081
- **Tenant Service:** http://localhost:8082
- **Product Service:** http://localhost:8083
- **Order Service:** http://localhost:8084
- **Notification Service:** http://localhost:8085

### API Documentation (Swagger)
- **Auth API:** http://localhost:8081/swagger-ui.html
- **Tenant API:** http://localhost:8082/swagger-ui.html
- **Product API:** http://localhost:8083/swagger-ui.html
- **Order API:** http://localhost:8084/swagger-ui.html

### Kafka Management (if using Docker)
- **Kafka UI:** http://localhost:8090

## 👤 Default Test Users

The application may include default test users. Check the auth service configuration or create users via the registration page.

### Sample User Roles:
- **Super Admin:** Full system access
- **Tenant Admin:** Tenant-specific management
- **Customer:** Shopping and order management

## 🧪 Testing the Application

### 1. User Registration/Login
1. Go to http://localhost:3000/register
2. Create a new user account
3. Login at http://localhost:3000/login

### 2. Shopping Flow
1. Browse products at http://localhost:3000/products
2. Add items to cart
3. Proceed to checkout
4. View orders at http://localhost:3000/orders

### 3. Real-time Features (if Kafka is running)
1. Login to the application
2. Open notifications at http://localhost:3000/notifications
3. Perform actions (place orders, update products) in another tab
4. Observe real-time notifications

### 4. Admin Features
1. Login as admin user
2. Access admin dashboard at http://localhost:3000/admin
3. Manage tenants at http://localhost:3000/admin/tenants

## 🔧 Troubleshooting

### Common Issues

1. **Port Conflicts:**
   ```bash
   # Check what's running on a port
   netstat -an | findstr :8080  # Windows
   lsof -i :8080               # Mac/Linux
   ```

2. **Database Connection Issues:**
   - Verify MySQL/MongoDB are running
   - Check connection strings in application.yml files
   - Ensure databases are created

3. **Frontend Build Issues:**
   ```bash
   # Clear node_modules and reinstall
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **Backend Service Issues:**
   ```bash
   # Check service logs
   mvn spring-boot:run -X  # Verbose logging
   ```

### Environment Variables

Create these environment files if needed:

**Backend (.env in each service directory):**
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ecommerce_auth
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=your-secret-key
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

**Frontend (.env in frontend directory):**
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=http://localhost:8080/ws
VITE_ENV=development
```

## 🚀 Production Deployment

For production deployment, consider:

1. **Use production databases** (MySQL/MongoDB clusters)
2. **Set up Kafka cluster** for scalability
3. **Configure load balancers** for backend services
4. **Build frontend for production:** `npm run build`
5. **Set up monitoring** and logging
6. **Configure SSL/TLS** certificates
7. **Set up CI/CD pipelines**

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [React Documentation](https://react.dev/)
- [Material-UI Documentation](https://mui.com/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Frontend Kafka Integration Guide](./frontend/KAFKA_INTEGRATION.md)

## 🆘 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are installed
3. Ensure all services are running
4. Check service logs for error messages
5. Verify database connections and Kafka setup

---

**Happy coding! 🎉**
