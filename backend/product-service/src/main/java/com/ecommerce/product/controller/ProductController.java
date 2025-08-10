package com.ecommerce.product.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-Tenant-ID") Long tenantId,
            @RequestHeader("X-User-ID") String createdBy) {
        try {
            Product product = productService.createProduct(request, tenantId, createdBy);
            return ResponseEntity.ok(ApiResponse.success(product, "Product created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-Tenant-ID") Long tenantId,
            @RequestHeader("X-User-ID") String updatedBy) {
        try {
            Product product = productService.updateProduct(id, request, tenantId, updatedBy);
            return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(
            @PathVariable String id,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            productService.deleteProduct(id, tenantId);
            return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(
            @PathVariable String id,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            Product product = productService.getProductById(id, tenantId);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductByIdPublic(@PathVariable String id) {
        try {
            Product product = productService.getProductByIdPublic(id);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(
            @RequestHeader("X-Tenant-ID") Long tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProductsByTenant(tenantId, pageable);
            return ResponseEntity.ok(ApiResponse.success(products.getContent()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Product>>> getActiveProducts(
            @RequestHeader("X-Tenant-ID") Long tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getActiveProductsByTenant(tenantId, pageable);
            return ResponseEntity.ok(ApiResponse.success(products.getContent()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(
            @PathVariable String category,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Product> products = productService.getProductsByCategory(tenantId, category);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeaturedProducts(
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Product> products = productService.getFeaturedProductsByTenant(tenantId);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProductsByName(
            @RequestParam String name,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Product> products = productService.searchProductsByName(tenantId, name);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/search/category")
    public ResponseEntity<ApiResponse<List<Product>>> searchProductsByCategory(
            @RequestParam String category,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Product> products = productService.searchProductsByCategory(tenantId, category);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByTags(
            @RequestParam List<String> tags,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            List<Product> products = productService.getProductsByTags(tenantId, tags);
            return ResponseEntity.ok(ApiResponse.success(products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<Product>> getProductBySku(
            @PathVariable String sku,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            return productService.getProductBySku(tenantId, sku)
                    .map(product -> ResponseEntity.ok(ApiResponse.success(product)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getProductCount(@RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            long count = productService.getProductCountByTenant(tenantId);
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Object>> updateStockQuantity(
            @PathVariable String id,
            @RequestParam Integer quantity,
            @RequestHeader("X-Tenant-ID") Long tenantId) {
        try {
            productService.updateStockQuantity(id, tenantId, quantity);
            return ResponseEntity.ok(ApiResponse.success(null, "Stock quantity updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 