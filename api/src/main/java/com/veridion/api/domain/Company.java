package com.veridion.api.domain;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Builder
@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "company_id_seq_generator")
  @SequenceGenerator(name = "company_id_seq_generator", sequenceName = "company_id_seq", allocationSize = 1)
  private Long id;

  private String domain;
  private String website;
  private String commercialName;
  private String legalName;

  @Type(StringArrayType.class)
  @Column(name = "all_names", columnDefinition = "varchar[]")
  private String[] allNames;

  private String address;

  @Type(StringArrayType.class)
  @Column(name = "phone_numbers", columnDefinition = "varchar[]")
  private String[] phoneNumbers;

  @Type(StringArrayType.class)
  @Column(name = "social_media_links", columnDefinition = "varchar[]")
  private String[] socialMediaLinks;

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;

  @Version
  private Long version;
}
