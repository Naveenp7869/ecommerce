# Multi-Tenant Ecommerce Application

A comprehensive multi-tenant ecommerce platform built with React + Vite frontend and Spring Boot microservices backend.

## Architecture

### Frontend
- **React 18** with **Vite** for fast development
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API calls
- **React Query** for state management

### Backend Microservices
- **Auth Service**: Authentication & Authorization (JWT)
- **Tenant Service**: Tenant management and registration
- **Product Service**: Product catalog management
- **Order Service**: Order processing and management
- **Notification Service**: Email notifications
- **Discovery Service**: Service discovery and health checks

### Database
- **MySQL**: For relational data (users, tenants, orders)
- **MongoDB**: For product catalog and flexible data

### Security
- **JWT Authentication**
- **Role-based access control** (Super Admin, Tenant, User)
- **CORS configuration**
- **Input validation and sanitization**

## Features

### Super Admin
- Invite tenants via email
- Approve/reject tenant registrations
- Monitor all tenants and their activities
- System-wide analytics

### Tenant
- Register after receiving invitation
- Manage product catalog
- Process orders
- View analytics for their store

### User
- Browse products by tenant
- Place orders
- View order history
- User profile management

## Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Docker & Docker Compose
- MySQL 8.0
- MongoDB 6.0

### Quick Start

1. **Clone the repository**
```bash
git clone <repository-url>
cd Ecommerce
```

2. **Start infrastructure**
```bash
docker-compose up -d
```

3. **Start backend services**
```bash
cd backend
./start-services.sh
```

4. **Start frontend**
```bash
cd frontend
npm install
npm run dev
```

## Service Communication

The application uses asynchronous communication between microservices via Apache Kafka:
- **Event-driven architecture** with Kafka topics and partitions
- **REST APIs** for synchronous operations
- **Service discovery** for dynamic service location

## API Documentation

Each service exposes its own API documentation at:
- Auth Service: `http://localhost:8081/swagger-ui.html`
- Tenant Service: `http://localhost:8082/swagger-ui.html`
- Product Service: `http://localhost:8083/swagger-ui.html`
- Order Service: `http://localhost:8084/swagger-ui.html`
- Notification Service: `http://localhost:8085/swagger-ui.html`

## Environment Variables

Create `.env` files in each service directory with appropriate configurations for:
- Database connections
- JWT secrets
- Email service credentials
- Service ports

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 