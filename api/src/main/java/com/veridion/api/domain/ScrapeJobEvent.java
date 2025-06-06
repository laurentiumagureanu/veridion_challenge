package com.veridion.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeJobEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "scrape_job_event_id_seq_generator")
  @SequenceGenerator(name = "scrape_job_event_id_seq_generator", sequenceName = "scrape_job_event_id_seq", allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "scrape_job_id", nullable = false)
  private ScrapeJob scrapeJob;

  @Enumerated(EnumType.STRING)
  private EventType eventType;

  private String eventDetails;

  @CreationTimestamp
  private Instant createdAt;

  @Version
  private Long version;

  public enum EventType {
    QUEUED,
    FAILED_TO_QUEUE,
    COMPLETED,
    FAILED
  }
} 