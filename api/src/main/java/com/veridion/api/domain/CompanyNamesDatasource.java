package com.veridion.api.domain;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyNamesDatasource {

  @Id
  private String domain;

  private String commercialName;
  private String legalName;

  @Type(StringArrayType.class)
  @Column(name = "all_names", columnDefinition = "varchar[]")
  private String[] allNames;

}
