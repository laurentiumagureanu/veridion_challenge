package com.veridion.api.rabbitmq;

import com.veridion.api.config.RabbitMQProperties;
import com.veridion.api.rabbitmq.dto.ScrapeJobRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {
  private final RabbitTemplate rabbitMQSender;
  private final RabbitMQProperties rabbitMQProperties;

  public void sendScrapeRequest(ScrapeJobRequest scrapeJobRequest) {
    log.info("Sending scrape request for domain: {} with job id: {}", 
        scrapeJobRequest.domain(), scrapeJobRequest.id());
    
    rabbitMQSender.convertAndSend(
        rabbitMQProperties.getScrapeExchange(),
        rabbitMQProperties.getScrapeRequestRoutingKey(),
        scrapeJobRequest
    );
    
    log.info("Successfully sent scrape request to queue for domain: {}", scrapeJobRequest.domain());
  }

}
