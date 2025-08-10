package com.ecommerce.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    private String id;
    
    @Indexed
    private Long tenantId;
    
    @Indexed
    private String name;
    
    private String description;
    
    @Indexed
    private String category;
    
    private String brand;
    
    private String sku;
    
    private BigDecimal price;
    
    private BigDecimal compareAtPrice;
    
    private Integer stockQuantity;
    
    private String unit;
    
    private List<String> images;
    
    private Map<String, Object> attributes;
    
    private List<String> tags;
    
    private Boolean isActive = true;
    
    private Boolean isFeatured = false;
    
    private Integer weight;
    
    private String weightUnit;
    
    private Map<String, String> dimensions;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
} 