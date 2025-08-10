package com.ecommerce.tenant.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.tenant.dto.TenantInvitationRequest;
import com.ecommerce.tenant.dto.TenantRegistrationRequest;
import com.ecommerce.tenant.model.Tenant;
import com.ecommerce.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TenantController {
    
    private final TenantService tenantService;
    
    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Object>> inviteTenant(
            @Valid @RequestBody TenantInvitationRequest request,
            @RequestHeader("X-User-ID") Long invitedBy) {
        try {
            tenantService.inviteTenant(request, invitedBy);
            return ResponseEntity.ok(ApiResponse.success(null, "Tenant invitation sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Tenant>> registerTenant(@Valid @RequestBody TenantRegistrationRequest request) {
        try {
            Tenant tenant = tenantService.registerTenant(request);
            return ResponseEntity.ok(ApiResponse.success(tenant, "Tenant registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Tenant>> approveTenant(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long approvedBy) {
        try {
            Tenant tenant = tenantService.approveTenant(id, approvedBy);
            return ResponseEntity.ok(ApiResponse.success(tenant, "Tenant approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Tenant>> rejectTenant(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long rejectedBy) {
        try {
            Tenant tenant = tenantService.rejectTenant(id, rejectedBy);
            return ResponseEntity.ok(ApiResponse.success(tenant, "Tenant rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Tenant>>> getAllTenants() {
        try {
            List<Tenant> tenants = tenantService.getAllTenants();
            return ResponseEntity.ok(ApiResponse.success(tenants));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Tenant>>> getTenantsByStatus(@PathVariable String status) {
        try {
            Tenant.Status tenantStatus = Tenant.Status.valueOf(status.toUpperCase());
            List<Tenant> tenants = tenantService.getTenantsByStatus(tenantStatus);
            return ResponseEntity.ok(ApiResponse.success(tenants));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tenant>> getTenantById(@PathVariable Long id) {
        try {
            Tenant tenant = tenantService.getTenantById(id);
            return ResponseEntity.ok(ApiResponse.success(tenant));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Tenant>> getTenantByEmail(@PathVariable String email) {
        try {
            Tenant tenant = tenantService.getTenantByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(tenant));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 