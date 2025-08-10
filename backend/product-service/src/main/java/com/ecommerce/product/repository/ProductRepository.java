package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByTenantId(Long tenantId);
    
    Page<Product> findByTenantId(Long tenantId, Pageable pageable);
    
    List<Product> findByTenantIdAndIsActiveTrue(Long tenantId);
    
    Page<Product> findByTenantIdAndIsActiveTrue(Long tenantId, Pageable pageable);
    
    List<Product> findByTenantIdAndCategory(Long tenantId, String category);
    
    List<Product> findByTenantIdAndIsFeaturedTrue(Long tenantId);
    
    @Query("{'tenantId': ?0, 'name': {$regex: ?1, $options: 'i'}}")
    List<Product> findByTenantIdAndNameContainingIgnoreCase(Long tenantId, String name);
    
    @Query("{'tenantId': ?0, 'category': {$regex: ?1, $options: 'i'}}")
    List<Product> findByTenantIdAndCategoryContainingIgnoreCase(Long tenantId, String category);
    
    @Query("{'tenantId': ?0, 'tags': {$in: ?1}}")
    List<Product> findByTenantIdAndTagsIn(Long tenantId, List<String> tags);
    
    Optional<Product> findByTenantIdAndSku(Long tenantId, String sku);
    
    boolean existsByTenantIdAndSku(Long tenantId, String sku);
    
    long countByTenantId(Long tenantId);
} 