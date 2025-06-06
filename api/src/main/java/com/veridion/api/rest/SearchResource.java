package com.veridion.api.rest;

import com.veridion.api.domain.CompanyDocument;
import com.veridion.api.rabbitmq.dto.CompanySearchRequest;
import com.veridion.api.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Operations for searching companies and related data")
public class SearchResource {

  private final SearchService searchService;

  @PostMapping("/companies")
  @Operation(summary = "Search companies", description = "Searches for companies based on the provided search criteria")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Search completed successfully",
          content = @Content(schema = @Schema(implementation = SearchHit.class))),
      @ApiResponse(responseCode = "400", description = "Invalid search parameters",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content)
  })
  public SearchHit<CompanyDocument> searchCompanies(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Company search request parameters",
          required = true,
          content = @Content(schema = @Schema(implementation = CompanySearchRequest.class)))
      @RequestBody CompanySearchRequest searchRequest) {
    return searchService.search(searchRequest);
  }

}
