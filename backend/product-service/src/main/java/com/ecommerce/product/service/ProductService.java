package com.ecommerce.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public Product createProduct(ProductRequest request, Long tenantId, String createdBy) {
        // Check if SKU already exists for this tenant
        if (request.getSku() != null && productRepository.existsByTenantIdAndSku(tenantId, request.getSku())) {
            throw new RuntimeException("SKU already exists for this tenant");
        }
        
        Product product = Product.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .brand(request.getBrand())
                .sku(request.getSku())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .stockQuantity(request.getStockQuantity())
                .unit(request.getUnit())
                .images(request.getImages())
                .attributes(request.getAttributes())
                .tags(request.getTags())
                .isActive(request.getIsActive())
                .isFeatured(request.getIsFeatured())
                .weight(request.getWeight())
                .weightUnit(request.getWeightUnit())
                .dimensions(request.getDimensions())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
        
        product = productRepository.save(product);
        
        // Publish event for other services
        publishProductCreatedEvent(product);
        
        return product;
    }
    
    public Product updateProduct(String productId, ProductRequest request, Long tenantId, String updatedBy) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Product does not belong to this tenant");
        }
        
        // Check if SKU already exists for this tenant (if changed)
        if (request.getSku() != null && !request.getSku().equals(product.getSku()) &&
                productRepository.existsByTenantIdAndSku(tenantId, request.getSku())) {
            throw new RuntimeException("SKU already exists for this tenant");
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUnit(request.getUnit());
        product.setImages(request.getImages());
        product.setAttributes(request.getAttributes());
        product.setTags(request.getTags());
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured());
        product.setWeight(request.getWeight());
        product.setWeightUnit(request.getWeightUnit());
        product.setDimensions(request.getDimensions());
        product.setUpdatedAt(LocalDateTime.now());
        product.setUpdatedBy(updatedBy);
        
        product = productRepository.save(product);
        
        // Publish event for other services
        publishProductUpdatedEvent(product);
        
        return product;
    }
    
    public void deleteProduct(String productId, Long tenantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Product does not belong to this tenant");
        }
        
        productRepository.delete(product);
        
        // Publish event for other services
        publishProductDeletedEvent(product);
    }
    
    public Product getProductById(String productId, Long tenantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Product does not belong to this tenant");
        }
        
        return product;
    }
    
    public Product getProductByIdPublic(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public List<Product> getAllProductsByTenant(Long tenantId) {
        return productRepository.findByTenantId(tenantId);
    }
    
    public Page<Product> getAllProductsByTenant(Long tenantId, Pageable pageable) {
        return productRepository.findByTenantId(tenantId, pageable);
    }
    
    public List<Product> getActiveProductsByTenant(Long tenantId) {
        return productRepository.findByTenantIdAndIsActiveTrue(tenantId);
    }
    
    public Page<Product> getActiveProductsByTenant(Long tenantId, Pageable pageable) {
        return productRepository.findByTenantIdAndIsActiveTrue(tenantId, pageable);
    }
    
    public List<Product> getProductsByCategory(Long tenantId, String category) {
        return productRepository.findByTenantIdAndCategory(tenantId, category);
    }
    
    public List<Product> getFeaturedProductsByTenant(Long tenantId) {
        return productRepository.findByTenantIdAndIsFeaturedTrue(tenantId);
    }
    
    public List<Product> searchProductsByName(Long tenantId, String name) {
        return productRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name);
    }
    
    public List<Product> searchProductsByCategory(Long tenantId, String category) {
        return productRepository.findByTenantIdAndCategoryContainingIgnoreCase(tenantId, category);
    }
    
    public List<Product> getProductsByTags(Long tenantId, List<String> tags) {
        return productRepository.findByTenantIdAndTagsIn(tenantId, tags);
    }
    
    public Optional<Product> getProductBySku(Long tenantId, String sku) {
        return productRepository.findByTenantIdAndSku(tenantId, sku);
    }
    
    public long getProductCountByTenant(Long tenantId) {
        return productRepository.countByTenantId(tenantId);
    }
    
    public void updateStockQuantity(String productId, Long tenantId, Integer quantity) {
        Product product = getProductById(productId, tenantId);
        product.setStockQuantity(quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
        
        // Publish stock update event
        publishStockUpdatedEvent(product);
    }
    
    private void publishProductCreatedEvent(Product product) {
        kafkaTemplate.send("product-events", "product.created", product);
        log.info("Published product created event for product: {}", product.getId());
    }
    
    private void publishProductUpdatedEvent(Product product) {
        kafkaTemplate.send("product-events", "product.updated", product);
        log.info("Published product updated event for product: {}", product.getId());
    }
    
    private void publishProductDeletedEvent(Product product) {
        kafkaTemplate.send("product-events", "product.deleted", product);
        log.info("Published product deleted event for product: {}", product.getId());
    }
    
    private void publishStockUpdatedEvent(Product product) {
        kafkaTemplate.send("product-events", "product.stock.updated", product);
        log.info("Published stock updated event for product: {}", product.getId());
    }
} 