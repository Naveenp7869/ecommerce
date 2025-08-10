package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public Order createOrder(OrderRequest request, Long userId, Long tenantId) {
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Create order items and calculate total
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    // Get product details from product service
                    var product = productService.getProductByIdPublic(item.getProductId());
                    
                    // Validate stock
                    if (product.getStockQuantity() < item.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName());
                    }
                    
                    BigDecimal unitPrice = product.getPrice();
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                    
                    return OrderItem.builder()
                            .productId(item.getProductId())
                            .productName(product.getName())
                            .quantity(item.getQuantity())
                            .unitPrice(unitPrice)
                            .totalPrice(totalPrice)
                            .build();
                })
                .collect(Collectors.toList());
        
        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create order
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .tenantId(tenantId)
                .status(Order.Status.PENDING)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .orderItems(orderItems)
                .build();
        
        // Set order reference in order items
        final Order finalOrder = order;
        orderItems.forEach(item -> item.setOrder(finalOrder));
        
        order = orderRepository.save(order);
        
        // Update product stock
        updateProductStock(orderItems);
        
        // Publish events
        publishOrderCreatedEvent(order);
        
        return order;
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, Order.Status status, Long tenantId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Order does not belong to this tenant");
        }
        
        order.setStatus(status);
        order = orderRepository.save(order);
        
        // Publish event
        publishOrderStatusUpdatedEvent(order);
        
        return order;
    }
    
    @Transactional
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus, Long tenantId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Order does not belong to this tenant");
        }
        
        order.setPaymentStatus(paymentStatus);
        order = orderRepository.save(order);
        
        // Publish event
        publishPaymentStatusUpdatedEvent(order);
        
        return order;
    }
    
    public Order getOrderById(Long orderId, Long tenantId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Order does not belong to this tenant");
        }
        
        return order;
    }
    
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }
    
    public List<Order> getOrdersByTenant(Long tenantId) {
        return orderRepository.findByTenantId(tenantId);
    }
    
    public Page<Order> getOrdersByTenant(Long tenantId, Pageable pageable) {
        return orderRepository.findByTenantId(tenantId, pageable);
    }
    
    public List<Order> getOrdersByUserAndTenant(Long userId, Long tenantId) {
        return orderRepository.findByUserIdAndTenantId(userId, tenantId);
    }
    
    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getOrdersByTenantAndStatus(Long tenantId, Order.Status status) {
        return orderRepository.findByTenantIdAndStatus(tenantId, status);
    }
    
    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }
    
    public List<Order> getOrdersByTenantAndPaymentStatus(Long tenantId, Order.PaymentStatus paymentStatus) {
        return orderRepository.findByTenantIdAndPaymentStatus(tenantId, paymentStatus);
    }
    
    private String generateOrderNumber() {
        String orderNumber;
        do {
            orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }
    
    private void updateProductStock(List<OrderItem> orderItems) {
        orderItems.forEach(item -> {
            try {
                var product = productService.getProductByIdPublic(item.getProductId());
                int newStock = product.getStockQuantity() - item.getQuantity();
                productService.updateStockQuantity(item.getProductId(), product.getTenantId(), newStock);
            } catch (Exception e) {
                log.error("Failed to update stock for product: {}", item.getProductId(), e);
            }
        });
    }
    
    private void publishOrderCreatedEvent(Order order) {
        kafkaTemplate.send("order-events", "order.created", order);
        log.info("Published order created event for order: {}", order.getOrderNumber());
    }
    
    private void publishOrderStatusUpdatedEvent(Order order) {
        kafkaTemplate.send("order-events", "order.status.updated", order);
        log.info("Published order status updated event for order: {}", order.getOrderNumber());
    }
    
    private void publishPaymentStatusUpdatedEvent(Order order) {
        kafkaTemplate.send("order-events", "order.payment.updated", order);
        log.info("Published payment status updated event for order: {}", order.getOrderNumber());
    }
} 