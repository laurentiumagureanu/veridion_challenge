package com.veridion.api.mapper;

import com.veridion.api.domain.ScrapeJob;
import com.veridion.api.rabbitmq.dto.ScrapeJobRequest;
import com.veridion.api.rabbitmq.dto.ScrapeJobResponse;
import com.veridion.api.rest.representation.ScrapeJobRepresentation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ScrapeJobMapper {

  ScrapeJobRequest toScrapeJobRequest(ScrapeJob scrapeJob);
  ScrapeJobRepresentation toRepresentation(ScrapeJob scrapeJob);
  ScrapeJob.Status mapScrapeJobResponseStatus(ScrapeJobResponse.Status status);
}
