package com.ecommerce.tenant.service;

import com.ecommerce.tenant.model.Tenant;
import com.ecommerce.tenant.model.TenantInvitation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public void sendTenantInvitation(TenantInvitation invitation, String tenantName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(invitation.getEmail());
            message.setSubject("Invitation to Join Ecommerce Platform");
            
            String invitationUrl = frontendUrl + "/register?token=" + invitation.getInvitationToken();
            
            message.setText(String.format(
                "Hello,\n\n" +
                "You have been invited to join our ecommerce platform as a tenant.\n\n" +
                "Tenant Name: %s\n" +
                "Invitation Link: %s\n\n" +
                "This invitation will expire in 7 days.\n\n" +
                "Best regards,\nEcommerce Platform Team",
                tenantName, invitationUrl
            ));
            
            mailSender.send(message);
            log.info("Tenant invitation email sent to: {}", invitation.getEmail());
        } catch (Exception e) {
            log.error("Failed to send tenant invitation email to: {}", invitation.getEmail(), e);
        }
    }
    
    public void sendTenantApproval(Tenant tenant) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(tenant.getEmail());
            message.setSubject("Tenant Registration Approved");
            
            message.setText(String.format(
                "Hello,\n\n" +
                "Congratulations! Your tenant registration has been approved.\n\n" +
                "Tenant Name: %s\n" +
                "Status: APPROVED\n\n" +
                "You can now log in to your account and start managing your products.\n\n" +
                "Best regards,\nEcommerce Platform Team",
                tenant.getName()
            ));
            
            mailSender.send(message);
            log.info("Tenant approval email sent to: {}", tenant.getEmail());
        } catch (Exception e) {
            log.error("Failed to send tenant approval email to: {}", tenant.getEmail(), e);
        }
    }
    
    public void sendTenantRejection(Tenant tenant) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(tenant.getEmail());
            message.setSubject("Tenant Registration Update");
            
            message.setText(String.format(
                "Hello,\n\n" +
                "We regret to inform you that your tenant registration has been rejected.\n\n" +
                "Tenant Name: %s\n" +
                "Status: REJECTED\n\n" +
                "If you have any questions, please contact our support team.\n\n" +
                "Best regards,\nEcommerce Platform Team",
                tenant.getName()
            ));
            
            mailSender.send(message);
            log.info("Tenant rejection email sent to: {}", tenant.getEmail());
        } catch (Exception e) {
            log.error("Failed to send tenant rejection email to: {}", tenant.getEmail(), e);
        }
    }
} 