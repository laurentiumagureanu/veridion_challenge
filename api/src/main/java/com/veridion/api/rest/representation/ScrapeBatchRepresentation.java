package com.veridion.api.rest.representation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Jacksonized
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
@Relation(value = "scrapeBatch", collectionRelation = "scrapeBatches")
public class ScrapeBatchRepresentation extends RepresentationModel<ScrapeJobRepresentation> {

  private Long id;
  private CollectionModel<ScrapeJobRepresentation> scrapeJobs;

}
