# RabbitMQ to Kafka Migration Guide

This document outlines the complete migration from RabbitMQ to Apache Kafka in the ecommerce microservices application.

## Changes Made

### 1. Infrastructure Changes

#### Docker Compose Updates
- **Removed**: RabbitMQ container
- **Added**: Kafka and Zookeeper containers using Confluent Platform
- **Ports**:
  - Kafka: 9092
  - Zookeeper: 2181
  - JMX Monitoring: 9997

#### New Docker Services
```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.4.0
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181
    ZOOKEEPER_TICK_TIME: 2000

kafka:
  image: confluentinc/cp-kafka:7.4.0
  ports:
    - "9092:9092"
    - "9997:9997"
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

### 2. Dependency Changes

#### Parent POM Updates
- **Removed**: `spring-boot-starter-amqp`
- **Added**: `spring-boot-starter-kafka` (version managed by Spring Boot)

#### All Service POMs Updated
- Auth Service
- Order Service
- Product Service
- Tenant Service
- Notification Service

### 3. Configuration Changes

#### Application.yml Updates
All services now use Kafka configuration instead of RabbitMQ:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false
    consumer:
      group-id: {service-name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.ecommerce.*"
        spring.json.add.type.headers: false
```

### 4. Code Changes

#### Common Configuration
- **Added**: `KafkaConfig.java` in common module for shared Kafka configuration

#### Service Updates

**OrderService**:
- Replaced `RabbitTemplate` with `KafkaTemplate<String, Object>`
- Updated event publishing methods:
  - `order-events` topic with keys: `order.created`, `order.status.updated`, `order.payment.updated`

**ProductService**:
- Replaced `RabbitTemplate` with `KafkaTemplate<String, Object>`
- Updated event publishing methods:
  - `product-events` topic with keys: `product.created`, `product.updated`, `product.deleted`, `product.stock.updated`

### 5. Topics Structure

#### Kafka Topics
- `order-events`: Order lifecycle events
- `product-events`: Product management events
- `tenant-events`: Tenant management events
- `notification-events`: Notification events

#### Event Types
- **Order Events**: `order.created`, `order.status.updated`, `order.payment.updated`
- **Product Events**: `product.created`, `product.updated`, `product.deleted`, `product.stock.updated`
- **Tenant Events**: `tenant.created`, `tenant.approved`, `tenant.rejected`
- **Notification Events**: Various notification types

### 6. Benefits of Migration

#### Performance
- **Higher Throughput**: Kafka handles much higher message throughput than RabbitMQ
- **Lower Latency**: Better performance for high-volume event streaming
- **Horizontal Scaling**: Better partition-based scaling

#### Reliability
- **Durability**: Messages are persisted to disk and replicated
- **Fault Tolerance**: Built-in replication and failover capabilities
- **Message Ordering**: Guaranteed ordering within partitions

#### Event Streaming
- **Event Sourcing**: Better support for event sourcing patterns
- **Stream Processing**: Native support for Kafka Streams
- **Real-time Analytics**: Better integration with analytics tools

### 7. Running the Application

#### Start Infrastructure
```bash
docker-compose up -d
```

This will start:
- MySQL (port 3306)
- MongoDB (port 27017)
- Kafka (port 9092)
- Zookeeper (port 2181)
- Redis (port 6379)

#### Start Services
```bash
cd backend
./start-services.sh
```

#### Verify Kafka Topics
You can use Kafka tools to verify topics are created:
```bash
# List topics
docker exec -it ecommerce-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Create topics manually if needed
docker exec -it ecommerce-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic order-events --partitions 3 --replication-factor 1
docker exec -it ecommerce-kafka kafka-topics --bootstrap-server localhost:9092 --create --topic product-events --partitions 3 --replication-factor 1
```

### 8. Monitoring and Debugging

#### JMX Monitoring
Kafka JMX metrics are available on port 9997 for monitoring tools.

#### Kafka Logs
```bash
# View Kafka logs
docker logs ecommerce-kafka

# View consumer lag
docker exec -it ecommerce-kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --all-groups
```

#### Application Logs
Each service logs Kafka events with INFO level, making it easy to track message flow.

### 9. Next Steps

#### Optional Enhancements
1. **Schema Registry**: For better message schema management
2. **Kafka Connect**: For database change data capture
3. **Kafka Streams**: For real-time stream processing
4. **KSQL**: For SQL-like stream processing

#### Production Considerations
1. **Multi-broker Setup**: For production, use multiple Kafka brokers
2. **Security**: Enable SASL/SSL for secure communication
3. **Monitoring**: Integrate with Prometheus and Grafana
4. **Backup**: Configure regular backups of Kafka data

## Migration Complete

The application has been successfully migrated from RabbitMQ to Apache Kafka, providing better scalability, durability, and event streaming capabilities for the microservices architecture.
