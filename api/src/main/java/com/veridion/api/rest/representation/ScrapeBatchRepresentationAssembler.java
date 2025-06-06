package com.veridion.api.rest.representation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.veridion.api.domain.ScrapeBatch;
import com.veridion.api.rest.ScrapeResource;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ScrapeBatchRepresentationAssembler extends RepresentationModelAssemblerSupport<ScrapeBatch, ScrapeBatchRepresentation> {

  private final ScrapeJobRepresentationAssembler scrapeJobRepresentationAssembler;

  public ScrapeBatchRepresentationAssembler(ScrapeJobRepresentationAssembler scrapeJobRepresentationAssembler) {
    super(ScrapeResource.class, ScrapeBatchRepresentation.class);
    this.scrapeJobRepresentationAssembler = scrapeJobRepresentationAssembler;
  }

  @Override
  public ScrapeBatchRepresentation toModel(ScrapeBatch entity) {
    var representation = ScrapeBatchRepresentation.builder()
        .id(entity.getId())
        .scrapeJobs(scrapeJobRepresentationAssembler.toCollectionModel(entity.getScrapeJobs()))
        .build();
    representation.add(linkTo(methodOn(ScrapeResource.class).getScrapeBatch(entity.getId())).withSelfRel());
    return representation;
  }
}
