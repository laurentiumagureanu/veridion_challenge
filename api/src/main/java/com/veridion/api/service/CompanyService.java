package com.veridion.api.service;

import com.veridion.api.domain.Company;
import com.veridion.api.domain.CompanyNamesDatasource;
import com.veridion.api.mapper.CompanyMapper;
import com.veridion.api.rabbitmq.dto.ScrapeJobResponse;
import com.veridion.api.repository.CompanyNamesDatasourceRepository;
import com.veridion.api.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final CompanyMapper companyMapper;
  private final CompanyNamesDatasourceRepository companyNamesDatasourceRepository;

  public Company createCompanyFromScrapeResult(ScrapeJobResponse scrapeJobResponse) {
    log.info("Creating company from scrape result for domain: {}", scrapeJobResponse.domain());
    
    if (!scrapeJobResponse.status().isSuccessful()) {
      log.info("Cannot create company from scrape job with status: {}", scrapeJobResponse.status());
    }

    Company company = companyMapper.mapScrapeJobResponse(scrapeJobResponse);
    Company savedCompany = saveCompany(company);
    log.info("Successfully created company with id: {} for domain: {}", savedCompany.getId(), scrapeJobResponse.domain());
    return savedCompany;
  }

  public Optional<CompanyNamesDatasource> getCompanyNamesDatasource(String domain) {
    log.info("Fetching company names datasource for domain: {}", domain);
    Optional<CompanyNamesDatasource> datasource = companyNamesDatasourceRepository.findById(domain);
    if (datasource.isPresent()) {
      log.info("Found company names datasource for domain: {}", domain);
    } else {
      log.info("No company names datasource found for domain: {}", domain);
    }
    return datasource;
  }

  public Company saveCompany(Company company) {
    log.info("Saving company for domain: {}", company.getDomain());
    Company savedCompany = companyRepository.save(company);
    log.info("Successfully saved company with id: {}", savedCompany.getId());
    return savedCompany;
  }

}
