package com.veridion.api.domain;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class ScrapeBatch {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "scrape_batch_id_seq_generator")
  @SequenceGenerator(name = "scrape_batch_id_seq_generator", sequenceName = "scrape_batch_id_seq", allocationSize = 1)
  private Long id;

  @Type(StringArrayType.class)
  @Column(name = "domains", columnDefinition = "varchar[]")
  private String[] domains;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @Version
  private Long version;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "batch" , cascade = CascadeType.ALL)
  @Builder.Default
  private List<ScrapeJob> scrapeJobs = new ArrayList<>();
} 