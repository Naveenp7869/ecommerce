# Kafka Integration Guide

This document explains how the frontend integrates with Kafka for real-time messaging and notifications.

## Architecture Overview

The frontend uses WebSocket (SockJS + STOMP) to connect to the Spring Boot backend, which acts as a bridge to Kafka topics. This allows for real-time bidirectional communication.

```
Frontend (React) ↔ WebSocket/STOMP ↔ Spring Boot ↔ Kafka Topics
```

## Backend Requirements

### 1. Spring Boot Dependencies

Add these dependencies to your Spring Boot services:

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-messaging</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### 2. WebSocket Configuration

Create a WebSocket configuration class:

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

### 3. Kafka Topics

Create these Kafka topics:

- `order-events` - For order-related events
- `product-events` - For product-related events  
- `tenant-events` - For tenant management events
- `notifications` - For user notifications

### 4. Message Controllers

Create controllers to handle WebSocket messages and forward to Kafka:

```java
@Controller
public class WebSocketController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/orders")
    public void handleOrderEvent(OrderEvent event) {
        kafkaTemplate.send("order-events", event);
    }

    @MessageMapping("/products")
    public void handleProductEvent(ProductEvent event) {
        kafkaTemplate.send("product-events", event);
    }

    @MessageMapping("/notifications")
    public void handleNotification(NotificationMessage notification) {
        kafkaTemplate.send("notifications", notification);
    }
}
```

### 5. Kafka Listeners

Create listeners to forward Kafka messages to WebSocket clients:

```java
@Component
public class KafkaWebSocketBridge {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "order-events")
    public void handleOrderEvent(OrderEvent event) {
        messagingTemplate.convertAndSendToUser(
            event.getCustomerId(), 
            "/queue/orders", 
            event
        );
    }

    @KafkaListener(topics = "product-events")
    public void handleProductEvent(ProductEvent event) {
        messagingTemplate.convertAndSend(
            "/topic/tenant/" + event.getTenantId() + "/products", 
            event
        );
    }

    @KafkaListener(topics = "tenant-events")
    public void handleTenantEvent(TenantEvent event) {
        messagingTemplate.convertAndSend("/topic/admin/tenants", event);
    }

    @KafkaListener(topics = "notifications")
    public void handleNotification(NotificationMessage notification) {
        if (notification.getUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                notification.getUserId(), 
                "/queue/notifications", 
                notification
            );
        }
    }
}
```

## Frontend Integration

### 1. Kafka Service

The `kafkaService.ts` handles all WebSocket connections and subscriptions:

```typescript
import kafkaService from '../services/kafkaService';

// Subscribe to order events
const unsubscribe = kafkaService.subscribeToOrderEvents(
  userId, 
  (orderEvent) => {
    console.log('Order event received:', orderEvent);
  }
);

// Clean up subscription
unsubscribe();
```

### 2. Real-time Notifications

The notification store manages real-time notifications:

```typescript
import { useNotificationStore } from '../stores/notificationStore';

const { notifications, addNotification, markAsRead } = useNotificationStore();
```

### 3. Event Types

The frontend handles these event types:

- `ORDER_CREATED` - New order placed
- `ORDER_UPDATED` - Order status changed
- `PRODUCT_UPDATED` - Product information changed
- `TENANT_APPROVED` - Tenant registration approved
- `NOTIFICATION` - General notifications

## Message Destinations

### User-Specific Destinations

- `/user/{userId}/queue/orders` - Personal order updates
- `/user/{userId}/queue/notifications` - Personal notifications

### Topic Destinations

- `/topic/tenant/{tenantId}/products` - Tenant product updates
- `/topic/admin/tenants` - Admin tenant management updates

## Environment Configuration

Configure these environment variables:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=http://localhost:8080/ws
```

## Testing Real-time Features

1. Start the Kafka cluster and Spring Boot services
2. Start the frontend development server
3. Login as different user types (user, tenant, admin)
4. Trigger events through the API or admin interface
5. Observe real-time updates in the UI

## Troubleshooting

### Connection Issues

- Check if WebSocket endpoint is accessible
- Verify CORS configuration
- Ensure Kafka is running and topics exist

### Message Delivery

- Check Kafka consumer groups
- Verify topic partitioning
- Monitor WebSocket connection status

### Performance

- Monitor WebSocket connection count
- Implement connection pooling for high load
- Consider message batching for frequent updates

## Security Considerations

1. **Authentication**: WebSocket connections should authenticate users
2. **Authorization**: Verify user permissions for topic subscriptions
3. **Message Validation**: Validate all incoming messages
4. **Rate Limiting**: Implement rate limiting for WebSocket messages

## Monitoring

Monitor these metrics:

- WebSocket connection count
- Message throughput
- Kafka consumer lag
- Failed message delivery
- Connection errors
