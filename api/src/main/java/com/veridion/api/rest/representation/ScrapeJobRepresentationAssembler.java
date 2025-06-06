package com.veridion.api.rest.representation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.veridion.api.domain.ScrapeJob;
import com.veridion.api.mapper.ScrapeJobMapper;
import com.veridion.api.rest.ScrapeResource;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScrapeJobRepresentationAssembler extends RepresentationModelAssemblerSupport<ScrapeJob, ScrapeJobRepresentation> {

  private final ScrapeJobMapper scrapeJobMapper;

  public ScrapeJobRepresentationAssembler(ScrapeJobMapper scrapeJobMapper) {
    super(ScrapeResource.class, ScrapeJobRepresentation.class);
    this.scrapeJobMapper = scrapeJobMapper;
  }

  @Override
  public ScrapeJobRepresentation toModel(ScrapeJob entity) {
    var representation = scrapeJobMapper.toRepresentation(entity);
    representation.add(linkTo(methodOn(ScrapeResource.class).getScrapeJob(entity.getId())).withSelfRel());
    Optional.ofNullable(entity.getBatchId())
        .ifPresent(batchId -> representation.add(
            linkTo(methodOn(ScrapeResource.class).getScrapeBatch(batchId)).withRel("batch")
        ));
    return representation;
  }
}
