package com.veridion.api.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.veridion.rabbitmq")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQProperties {
  private String scrapeExchange;
  private String scrapeRequestRoutingKey;
  private String scrapeResultQueue;
}
