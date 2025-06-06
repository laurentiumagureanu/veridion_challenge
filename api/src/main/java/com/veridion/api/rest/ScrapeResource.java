package com.veridion.api.rest;

import com.veridion.api.rest.exception.NotFoundException;
import com.veridion.api.rest.representation.ScrapeBatchRepresentation;
import com.veridion.api.rest.representation.ScrapeBatchRepresentationAssembler;
import com.veridion.api.rest.representation.ScrapeBatchRequest;
import com.veridion.api.rest.representation.ScrapeJobRepresentation;
import com.veridion.api.rest.representation.ScrapeJobRepresentationAssembler;
import com.veridion.api.rest.representation.ScrapeRequest;
import com.veridion.api.service.ScrapeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scrape")
@RequiredArgsConstructor
@Tag(name = "Scrape", description = "Operations for managing scraping jobs and batches")
public class ScrapeResource {

  private final ScrapeService scrapeService;
  private final ScrapeJobRepresentationAssembler scrapeJobRepresentationAssembler;
  private final ScrapeBatchRepresentationAssembler scrapeBatchRepresentationAssembler;

  @PostMapping("/jobs")
  @Operation(summary = "Start a new scrape job", description = "Creates and starts a new scraping job with the provided request parameters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scrape job created successfully",
          content = @Content(schema = @Schema(implementation = ScrapeJobRepresentation.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content)
  })
  public ScrapeJobRepresentation startScrapeJob(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Scrape job request parameters",
          required = true,
          content = @Content(schema = @Schema(implementation = ScrapeRequest.class)))
      @RequestBody ScrapeRequest request) {
    var scrapeJob = scrapeService.createScrapeJob(request);
    return scrapeJobRepresentationAssembler.toModel(scrapeJob);
  }

  @GetMapping("/jobs/{id}")
  @Operation(summary = "Get scrape job by ID", description = "Retrieves a specific scrape job by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scrape job found",
          content = @Content(schema = @Schema(implementation = ScrapeJobRepresentation.class))),
      @ApiResponse(responseCode = "404", description = "Scrape job not found",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content)
  })
  public ScrapeJobRepresentation getScrapeJob(
      @Parameter(description = "ID of the scrape job to retrieve", required = true)
      @PathVariable Long id) {
    return scrapeService.getScrapeJob(id)
        .map(scrapeJobRepresentationAssembler::toModel)
        .orElseThrow(() -> new NotFoundException(id, "Scrape Job"));
  }

  @PostMapping("/batches")
  @Operation(summary = "Start a new scrape batch", description = "Creates and starts a new scraping batch with the provided request parameters")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scrape batch created successfully",
          content = @Content(schema = @Schema(implementation = ScrapeBatchRepresentation.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content)
  })
  public ScrapeBatchRepresentation startScrapeBatch(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Scrape batch request parameters",
          required = true,
          content = @Content(schema = @Schema(implementation = ScrapeBatchRequest.class)))
      @RequestBody ScrapeBatchRequest request) {
    var scrapeBatch = scrapeService.createScrapeBatch(request);
    return scrapeBatchRepresentationAssembler.toModel(scrapeBatch);
  }

  @GetMapping("/batches/{batchId}")
  @Operation(summary = "Get scrape batch by ID", description = "Retrieves a specific scrape batch by its ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Scrape batch found",
          content = @Content(schema = @Schema(implementation = ScrapeBatchRepresentation.class))),
      @ApiResponse(responseCode = "404", description = "Scrape batch not found",
          content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal server error",
          content = @Content)
  })
  public ScrapeBatchRepresentation getScrapeBatch(
      @Parameter(description = "ID of the scrape batch to retrieve", required = true)
      @PathVariable Long batchId) {
    return scrapeService.getScrapeBatch(batchId)
        .map(scrapeBatchRepresentationAssembler::toModel)
        .orElseThrow(() -> new NotFoundException(batchId, "Scrape Batch"));
  }

}
