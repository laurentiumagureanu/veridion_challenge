package com.veridion.api.mapper;

import com.veridion.api.domain.Company;
import com.veridion.api.domain.CompanyDocument;
import com.veridion.api.rabbitmq.dto.ScrapeJobResponse;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CompanyMapper {

  @Mappings({@Mapping(target = "id", ignore = true)})
  Company mapScrapeJobResponse(ScrapeJobResponse scrapeJobResponse);
  CompanyDocument toCompanyDocument(Company company);
}
