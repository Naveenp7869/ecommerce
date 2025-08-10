package com.ecommerce.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final JavaMailSender mailSender;
    
    @KafkaListener(topics = "tenant-events", groupId = "notification-service")
    public void handleTenantEvents(Object event) {
        log.info("Received tenant event: {}", event);
        // Handle different tenant events
        // This would be implemented based on the event type
    }
    
    @KafkaListener(topics = "product-events", groupId = "notification-service")
    public void handleProductEvents(Object event) {
        log.info("Received product event: {}", event);
        // Handle different product events
        // This would be implemented based on the event type
    }
    
    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderEvents(Object event) {
        log.info("Received order event: {}", event);
        // Handle different order events
        // This would be implemented based on the event type
    }
    
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
} 