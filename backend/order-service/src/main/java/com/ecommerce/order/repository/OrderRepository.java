package com.ecommerce.order.repository;

import com.ecommerce.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(Long userId);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByTenantId(Long tenantId);
    
    Page<Order> findByTenantId(Long tenantId, Pageable pageable);
    
    List<Order> findByUserIdAndTenantId(Long userId, Long tenantId);
    
    List<Order> findByStatus(Order.Status status);
    
    List<Order> findByTenantIdAndStatus(Long tenantId, Order.Status status);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> findByTenantIdAndPaymentStatus(Long tenantId, Order.PaymentStatus paymentStatus);
    
    boolean existsByOrderNumber(String orderNumber);
} 