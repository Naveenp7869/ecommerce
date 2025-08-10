package com.ecommerce.tenant.service;

import com.ecommerce.tenant.dto.TenantInvitationRequest;
import com.ecommerce.tenant.dto.TenantRegistrationRequest;
import com.ecommerce.tenant.model.Tenant;
import com.ecommerce.tenant.model.TenantInvitation;
import com.ecommerce.tenant.repository.TenantInvitationRepository;
import com.ecommerce.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {
    
    private final TenantRepository tenantRepository;
    private final TenantInvitationRepository invitationRepository;
    private final EmailService emailService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public TenantInvitation inviteTenant(TenantInvitationRequest request, Long invitedBy) {
        // Check if tenant already exists
        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Tenant with this email already exists");
        }
        
        // Check if invitation already exists
        Optional<TenantInvitation> existingInvitation = invitationRepository
                .findByEmailAndStatus(request.getEmail(), TenantInvitation.Status.PENDING);
        if (existingInvitation.isPresent()) {
            throw new RuntimeException("Invitation already sent to this email");
        }
        
        // Create invitation
        TenantInvitation invitation = TenantInvitation.builder()
                .email(request.getEmail())
                .invitationToken(UUID.randomUUID().toString())
                .invitedBy(invitedBy)
                .status(TenantInvitation.Status.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7)) // 7 days expiry
                .build();
        
        invitation = invitationRepository.save(invitation);
        
        // Send invitation email
        emailService.sendTenantInvitation(invitation, request.getTenantName());
        
        // Publish event for notification service
        publishTenantInvitedEvent(invitation);
        
        return invitation;
    }
    
    @Transactional
    public Tenant registerTenant(TenantRegistrationRequest request) {
        // Validate invitation token
        TenantInvitation invitation = invitationRepository.findByInvitationToken(request.getInvitationToken())
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        
        if (invitation.getStatus() != TenantInvitation.Status.PENDING) {
            throw new RuntimeException("Invitation has already been used or expired");
        }
        
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation has expired");
        }
        
        if (!invitation.getEmail().equals(request.getEmail())) {
            throw new RuntimeException("Email does not match invitation");
        }
        
        // Create tenant
        Tenant tenant = Tenant.builder()
                .name(request.getTenantName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(Tenant.Status.PENDING)
                .build();
        
        tenant = tenantRepository.save(tenant);
        
        // Update invitation status
        invitation.setStatus(TenantInvitation.Status.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
        
        // Publish event for auth service to create user
        publishTenantRegisteredEvent(tenant, request);
        
        return tenant;
    }
    
    @Transactional
    public Tenant approveTenant(Long tenantId, Long approvedBy) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        
        if (tenant.getStatus() != Tenant.Status.PENDING) {
            throw new RuntimeException("Tenant is not in pending status");
        }
        
        tenant.setStatus(Tenant.Status.APPROVED);
        tenant.setApprovedAt(LocalDateTime.now());
        tenant.setApprovedBy(approvedBy);
        
        tenant = tenantRepository.save(tenant);
        
        // Send approval email
        emailService.sendTenantApproval(tenant);
        
        // Publish event for notification service
        publishTenantApprovedEvent(tenant);
        
        return tenant;
    }
    
    @Transactional
    public Tenant rejectTenant(Long tenantId, Long rejectedBy) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        
        if (tenant.getStatus() != Tenant.Status.PENDING) {
            throw new RuntimeException("Tenant is not in pending status");
        }
        
        tenant.setStatus(Tenant.Status.REJECTED);
        tenant.setApprovedBy(rejectedBy);
        
        tenant = tenantRepository.save(tenant);
        
        // Send rejection email
        emailService.sendTenantRejection(tenant);
        
        return tenant;
    }
    
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
    
    public List<Tenant> getTenantsByStatus(Tenant.Status status) {
        return tenantRepository.findByStatus(status);
    }
    
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }
    
    public Tenant getTenantByEmail(String email) {
        return tenantRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }
    
    private void publishTenantInvitedEvent(TenantInvitation invitation) {
        // Publish to Kafka for notification service
        kafkaTemplate.send("tenant-events", "tenant.invited", invitation);
    }
    
    private void publishTenantRegisteredEvent(Tenant tenant, TenantRegistrationRequest request) {
        // Publish to Kafka for auth service
        kafkaTemplate.send("tenant-events", "tenant.registered", 
                new TenantRegistrationEvent(tenant, request));
    }
    
    private void publishTenantApprovedEvent(Tenant tenant) {
        // Publish to Kafka for notification service
        kafkaTemplate.send("tenant-events", "tenant.approved", tenant);
    }
    
    // Event class for tenant registration
    public static class TenantRegistrationEvent {
        private Tenant tenant;
        private TenantRegistrationRequest request;
        
        public TenantRegistrationEvent(Tenant tenant, TenantRegistrationRequest request) {
            this.tenant = tenant;
            this.request = request;
        }
        
        // Getters and setters
        public Tenant getTenant() { return tenant; }
        public void setTenant(Tenant tenant) { this.tenant = tenant; }
        public TenantRegistrationRequest getRequest() { return request; }
        public void setRequest(TenantRegistrationRequest request) { this.request = request; }
    }
} 