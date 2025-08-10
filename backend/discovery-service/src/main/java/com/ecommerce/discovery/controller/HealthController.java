package com.ecommerce.discovery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "discovery-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServices() {
        Map<String, Object> services = new HashMap<>();
        services.put("auth-service", "http://localhost:8081");
        services.put("tenant-service", "http://localhost:8082");
        services.put("product-service", "http://localhost:8083");
        services.put("order-service", "http://localhost:8084");
        services.put("notification-service", "http://localhost:8085");
        services.put("discovery-service", "http://localhost:8086");
        return ResponseEntity.ok(services);
    }
} 