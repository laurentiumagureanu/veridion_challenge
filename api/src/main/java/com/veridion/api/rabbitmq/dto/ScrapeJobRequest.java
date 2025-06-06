package com.veridion.api.rabbitmq.dto;

/**
 * Request DTO for scrape job operations.
 */
public record ScrapeJobRequest(
    Long id,
    String domain
) {
  
  public ScrapeJobRequest {
    if (domain == null || domain.isBlank()) {
      throw new IllegalArgumentException("Domain cannot be null or blank");
    }
  }
}
