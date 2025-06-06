package com.veridion.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeJob {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "scrape_job_id_seq_generator")
  @SequenceGenerator(name = "scrape_job_id_seq_generator", sequenceName = "scrape_job_id_seq", allocationSize = 1)
  private Long id;

  private String domain;

  @Enumerated(EnumType.STRING)
  private Status status;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "batch_id")
  private ScrapeBatch batch;

  @Column(name = "batch_id", updatable = false, insertable = false)
  private Long batchId;

  @Version
  private Long version;

  @OneToMany(mappedBy = "scrapeJob", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<ScrapeJobEvent> events = new ArrayList<>();

  public enum Status {
    CREATED,
    QUEUED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
  }
} 