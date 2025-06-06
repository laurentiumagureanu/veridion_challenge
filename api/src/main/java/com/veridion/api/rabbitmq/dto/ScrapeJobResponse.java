package com.veridion.api.rabbitmq.dto;

import java.util.List;

/**
 * Response DTO for scrape job operations.
 */
public record ScrapeJobResponse(
    Long id,
    String domain,
    String website,
    Status status,
    String address,
    List<String> socialMediaLinks,
    List<String> phoneNumbers
) {
  
  public ScrapeJobResponse {
    if (id == null) {
      throw new IllegalArgumentException("ID cannot be null");
    }
    if (domain == null || domain.isBlank()) {
      throw new IllegalArgumentException("Domain cannot be null or blank");
    }
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
  }

  public enum Status {
    COMPLETED,
    FAILED;
    
    public boolean isSuccessful() {
      return this == COMPLETED;
    }
  }
}
