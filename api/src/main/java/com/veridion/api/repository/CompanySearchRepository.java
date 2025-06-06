package com.veridion.api.repository;

import com.veridion.api.domain.CompanyDocument;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySearchRepository extends ElasticsearchRepository<CompanyDocument, Long> {

  @Query("""
    {
      "query" : {
        "bool": {
          "must": [
            { "match" : {"allNames": "?0"} },
          "should": [
            { "fuzzy": { "website": "?1" } },
            { "fuzzy": { "phones": "?2" } },
            { "fuzzy": { "socialMediaLinks": "?3" } }
          ]
        }
      }
    }
  """)
  SearchHits<CompanyDocument> findCompany(String name, String phone, String website, String socialMediaLink);

}
