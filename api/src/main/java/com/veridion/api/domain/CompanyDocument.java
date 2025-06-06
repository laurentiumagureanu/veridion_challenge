package com.veridion.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Document(indexName = "veridion_data")
public class CompanyDocument {

  @Id
  private Long id;

  @Field(type = FieldType.Keyword)
  private String domain;

  @Field(type = FieldType.Keyword)
  private String website;

  @Field(type = FieldType.Keyword)
  private String commercialName;

  @Field(type = FieldType.Keyword)
  private String legalName;

  @Field(type = FieldType.Keyword)
  private String[] allNames;

  @Field(type = FieldType.Text)
  private String address;

  @Field(type = FieldType.Text)
  private String[] phoneNumbers;

  @Field(type = FieldType.Text)
  private String[] socialMediaLinks;

  @Version
  private Long version;
}
