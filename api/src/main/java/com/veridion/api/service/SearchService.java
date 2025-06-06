package com.veridion.api.service;

import com.veridion.api.domain.CompanyDocument;
import com.veridion.api.rabbitmq.dto.CompanySearchRequest;
import com.veridion.api.repository.CompanySearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

  private final CompanySearchRepository companySearchRepository;
  private final ElasticsearchTemplate searchOperations;

  @Value("${com.veridion.search.fuzziness-level:2}")
  private String fuzzinessLevel;

  public void store(CompanyDocument companyDocument) {
    log.info("Storing company document for domain: {}", companyDocument.getDomain());
    companySearchRepository.save(companyDocument);
    log.info("Successfully stored company document with id: {} for domain: {}", 
        companyDocument.getId(), companyDocument.getDomain());
  }

  public SearchHit<CompanyDocument> search(CompanySearchRequest companySearchRequest) {
    log.info("Performing company search with request: name={}, website={}, phone={}, socialMedia={}", 
        companySearchRequest.name(), companySearchRequest.website(), 
        companySearchRequest.phone(), companySearchRequest.socialMediaLink());
    
    if (companySearchRequest.isEmpty()) {
      log.warn("Search request cannot be empty");
      throw new IllegalArgumentException("Search request cannot be empty");
    }

    var query = NativeQuery.builder().withQuery(q -> q.bool(b -> b.must(m -> this.createMatchQuery(m, companySearchRequest)))).build();
    SearchHit<CompanyDocument> result = searchOperations.searchOne(query, CompanyDocument.class);
    
    if (result != null) {
      log.info("Search completed successfully, found company: {}", result.getContent().getDomain());
    } else {
      log.info("Search completed, no results found");
    }
    
    return result;
  }

  private Query.Builder createMatchQuery(Query.Builder queryBuilder, CompanySearchRequest companySearchRequest) {
    log.debug("Building search query with criteria count: {}", 
        (companySearchRequest.name() != null ? 1 : 0) +
        (companySearchRequest.website() != null ? 1 : 0) +
        (companySearchRequest.phone() != null ? 1 : 0) +
        (companySearchRequest.socialMediaLink() != null ? 1 : 0));
    
    if (companySearchRequest.name() != null) {
      queryBuilder.match(mm -> mm.field("allNames")
          .query(companySearchRequest.name())
          .fuzziness(fuzzinessLevel));
    }

    if (companySearchRequest.website() != null) {
      queryBuilder.match(mm -> mm.field("website")
          .query(companySearchRequest.website())
          .fuzziness(fuzzinessLevel));
    }

    if (companySearchRequest.phone() != null) {
      queryBuilder.match(mm -> mm.field("phoneNumbers")
          .query(companySearchRequest.phone())
          .fuzziness(fuzzinessLevel));
    }

    if (companySearchRequest.socialMediaLink() != null) {
      queryBuilder.match(mm -> mm.field("socialMediaLinks")
          .query(companySearchRequest.socialMediaLink())
          .fuzziness(fuzzinessLevel));
    }

    return queryBuilder;
  }

}
