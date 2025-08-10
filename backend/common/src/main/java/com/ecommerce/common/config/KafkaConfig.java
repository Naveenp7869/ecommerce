package com.ecommerce.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Spring Boot auto-configuration will handle the Kafka configuration
    // based on application.yml properties
}
