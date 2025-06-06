package com.veridion.api.rest.representation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;
import java.util.List;

@Jacksonized
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
@Relation(value = "scrapeJob", collectionRelation = "scrapeJobs")
public class ScrapeJobRepresentation extends RepresentationModel<ScrapeJobRepresentation> {
  private Long id;
  private String domain;
  private Status status;
  private Instant createdAt;
  private Instant updatedAt;
  private Long batchId;
  private List<ScrapeJobEvent> events;

  public enum Status {
    CREATED,
    QUEUED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
  }

  public record ScrapeJobEvent(
      Long id,
      EventType eventType,
      String eventDetails,
      Instant createdAt
  ) {
    
    public ScrapeJobEvent {
      // Compact constructor validation
      if (eventType == null) {
        throw new IllegalArgumentException("Event type cannot be null");
      }
    }

    public enum EventType {
      QUEUED,
      FAILED_TO_QUEUE,
      COMPLETED,
      FAILED
    }
  }
}
