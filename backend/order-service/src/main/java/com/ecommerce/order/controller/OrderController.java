package com.ecommerce.order.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order order = orderService.createOrder(request, userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(order, "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order.Status orderStatus = Order.Status.valueOf(status.toUpperCase());
            Order order = orderService.updateOrderStatus(id, orderStatus, tenantId);
            return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<Order>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order.PaymentStatus status = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
            Order order = orderService.updatePaymentStatus(id, status, tenantId);
            return ResponseEntity.ok(ApiResponse.success(order, "Payment status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(
            @PathVariable Long id,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order order = orderService.getOrderById(id, tenantId);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByNumber(orderNumber);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUser(
            @RequestHeader("X-User-ID") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByUser(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(orders.getContent()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/tenant")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByTenant(
            @RequestHeader("X-Tenant-ID") Long tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByTenant(tenantId, pageable);
            return ResponseEntity.ok(ApiResponse.success(orders.getContent()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user-tenant")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUserAndTenant(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Order> orders = orderService.getOrdersByUserAndTenant(userId, tenantId);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable String status) {
        try {
            Order.Status orderStatus = Order.Status.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/tenant/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByTenantAndStatus(
            @PathVariable String status,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order.Status orderStatus = Order.Status.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByTenantAndStatus(tenantId, orderStatus);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/payment/{paymentStatus}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        try {
            Order.PaymentStatus status = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
            List<Order> orders = orderService.getOrdersByPaymentStatus(status);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/tenant/payment/{paymentStatus}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByTenantAndPaymentStatus(
            @PathVariable String paymentStatus,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Order.PaymentStatus status = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
            List<Order> orders = orderService.getOrdersByTenantAndPaymentStatus(tenantId, status);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 