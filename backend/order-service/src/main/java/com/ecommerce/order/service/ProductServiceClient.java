package com.ecommerce.order.service;

import com.ecommerce.order.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient implements ProductService {
    
    private final RestTemplate restTemplate;
    
    @Value("${app.product-service.url:http://localhost:8083}")
    private String productServiceUrl;
    
    @Override
    public Product getProductByIdPublic(String productId) {
        try {
            String url = productServiceUrl + "/api/products/public/" + productId;
            ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get product by ID: {}", productId, e);
            throw new RuntimeException("Failed to get product information");
        }
    }
    
    @Override
    public void updateStockQuantity(String productId, Long tenantId, Integer quantity) {
        try {
            String url = productServiceUrl + "/api/products/" + productId + "/stock?quantity=" + quantity;
            restTemplate.put(url, null);
        } catch (Exception e) {
            log.error("Failed to update stock for product: {}", productId, e);
            throw new RuntimeException("Failed to update product stock");
        }
    }
} 