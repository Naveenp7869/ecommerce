package com.ecommerce.tenant.repository;

import com.ecommerce.tenant.model.TenantInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantInvitationRepository extends JpaRepository<TenantInvitation, Long> {
    Optional<TenantInvitation> findByInvitationToken(String token);
    Optional<TenantInvitation> findByEmailAndStatus(String email, TenantInvitation.Status status);
    List<TenantInvitation> findByStatusAndExpiresAtBefore(TenantInvitation.Status status, LocalDateTime dateTime);
} 