package com.ecommerce.order.service;

import com.ecommerce.order.model.Product;

public interface ProductService {
    Product getProductByIdPublic(String productId);
    void updateStockQuantity(String productId, Long tenantId, Integer quantity);
} 