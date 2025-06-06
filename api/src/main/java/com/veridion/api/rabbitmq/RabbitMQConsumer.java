package com.veridion.api.rabbitmq;

import com.rabbitmq.client.Channel;
import com.veridion.api.mapper.CompanyMapper;
import com.veridion.api.mapper.ScrapeJobMapper;
import com.veridion.api.rabbitmq.dto.ScrapeJobResponse;
import com.veridion.api.service.CompanyService;
import com.veridion.api.service.ScrapeService;
import com.veridion.api.service.SearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

  private final CompanyService companyService;
  private final ScrapeService scrapeService;
  private final SearchService searchService;
  private final ScrapeJobMapper scrapeJobMapper;
  private final CompanyMapper companyMapper;

  @RabbitListener(queues = "#{rabbitMQProperties.scrapeResultQueue}", messageConverter = "jsonConverter", ackMode = "MANUAL")
  public void receiveScrapeJobResult(ScrapeJobResponse result, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    log.info("Received scrape job result for domain: {} with status: {}", result.domain(), result.status());
    
    try {
      if (result.status().isSuccessful()) {
        log.info("Processing completed scrape job for domain: {}", result.domain());
        var company = companyService.createCompanyFromScrapeResult(result);
        companyService.getCompanyNamesDatasource(result.domain())
            .ifPresentOrElse(datasource -> {
              log.info("Found company names datasource for domain: {}, updating company with additional names", result.domain());
              var finalCompany = companyService.saveCompany(
                  company
                      .withCommercialName(datasource.getCommercialName())
                      .withLegalName(datasource.getLegalName())
                      .withAllNames(datasource.getAllNames())
              );
              searchService.store(companyMapper.toCompanyDocument(finalCompany));
            }, () -> {
              log.info("No company names datasource found for domain: {}, storing company as-is", result.domain());
              searchService.store(companyMapper.toCompanyDocument(company));
            });
      } else {
        log.info("Scrape job completed with non-success status: {} for domain: {}", result.status(), result.domain());
      }

      scrapeService.updateScrapeJobStatus(result.id(), scrapeJobMapper.mapScrapeJobResponseStatus(result.status()));
      channel.basicAck(tag, false);
      log.info("Successfully processed and acknowledged scrape job result for domain: {}", result.domain());
    } catch (IOException e) {
      log.error("Failed to acknowledge message for domain: {} - {}", result.domain(), e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
