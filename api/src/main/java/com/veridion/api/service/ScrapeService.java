package com.veridion.api.service;

import com.veridion.api.domain.ScrapeBatch;
import com.veridion.api.domain.ScrapeJob;
import com.veridion.api.domain.ScrapeJobEvent;
import com.veridion.api.mapper.ScrapeJobMapper;
import com.veridion.api.rabbitmq.RabbitMQProducer;
import com.veridion.api.repository.ScrapeBatchRepository;
import com.veridion.api.repository.ScrapeJobEventRepository;
import com.veridion.api.repository.ScrapeJobRepository;
import com.veridion.api.rest.representation.ScrapeBatchRequest;
import com.veridion.api.rest.representation.ScrapeRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapeService {

  private final ScrapeJobRepository scrapeJobRepository;
  private final ScrapeJobEventRepository scrapeJobEventRepository;
  private final ScrapeBatchRepository scrapeBatchRepository;
  private final ScrapeJobMapper scrapeJobMapper;
  private final RabbitMQProducer rabbitMQProducer;

  public ScrapeJob createScrapeJob(ScrapeRequest request) {
    log.info("Creating scrape job for domain: {}", request.domain());
    var scrapeJob = addScrapeJob(request.domain());
    log.info("Successfully created scrape job with id: {} for domain: {}", 
             scrapeJob.getId(), request.domain());
    return scrapeJob;
  }

  public ScrapeBatch createScrapeBatch(ScrapeBatchRequest request) {
    log.info("Creating scrape batch for {} domains", request.domains().length);
    
    final ScrapeBatch batch = ScrapeBatch.builder()
        .domains(request.domains())
        .build();

    // Create and assign scrape jobs using modern stream API
    var scrapeJobs = Arrays.stream(request.domains())
        .map(domain -> createScrapeJobForBatch(domain, batch))
        .toList(); // Java 16+ feature

    batch.setScrapeJobs(scrapeJobs);
    var finalBatch = scrapeBatchRepository.save(batch);
    
    log.info("Successfully created scrape batch with id: {} containing {} jobs", 
             finalBatch.getId(), finalBatch.getScrapeJobs().size());
    
    // Send it to queue asynchronously
    sendScrapeBatchToQueueAsync(finalBatch);
    return finalBatch;
  }

  public void updateScrapeJobStatus(Long id, ScrapeJob.Status status) {
    log.info("Updating scrape job status for id: {} to status: {}", id, status);
    
    scrapeJobRepository.findById(id).ifPresentOrElse(
        scrapeJob -> updateJobWithStatus(scrapeJob, status),
        () -> log.warn("Scrape job with id {} not found for status update", id)
    );
  }

  public Optional<ScrapeJob> getScrapeJob(Long id) {
    log.info("Fetching scrape job with id: {}", id);
    return scrapeJobRepository.findById(id);
  }

  public Optional<ScrapeBatch> getScrapeBatch(Long id) {
    log.info("Fetching scrape batch with id: {}", id);
    return scrapeBatchRepository.findById(id);
  }

  private ScrapeJob createScrapeJobForBatch(String domain, ScrapeBatch batch) {
    return ScrapeJob.builder()
        .domain(domain)
        .batch(batch)
        .status(ScrapeJob.Status.CREATED)
        .build();
  }

  private void updateJobWithStatus(ScrapeJob scrapeJob, ScrapeJob.Status status) {
    scrapeJob.setStatus(status);
    var updatedJob = scrapeJobRepository.save(scrapeJob);
    
    // Create event based on status using switch expression
    var eventType = switch (status) {
      case COMPLETED -> ScrapeJobEvent.EventType.COMPLETED;
      case FAILED -> ScrapeJobEvent.EventType.FAILED;
      case QUEUED -> ScrapeJobEvent.EventType.QUEUED;
      case CREATED, IN_PROGRESS -> null; // No event needed for these statuses
    };
    
    if (eventType != null) {
      var event = ScrapeJobEvent.builder()
          .scrapeJob(updatedJob)
          .eventType(eventType)
          .build();
      scrapeJobEventRepository.save(event);
    }
    
    log.info("Successfully updated scrape job id: {} to status: {}", 
             updatedJob.getId(), status);
  }

  private ScrapeJob addScrapeJob(String domain) {
    log.info("Adding scrape job for domain: {}", domain);
    
    var scrapeJob = ScrapeJob.builder()
        .domain(domain)
        .status(ScrapeJob.Status.CREATED)
        .build();
    
    var savedJob = scrapeJobRepository.save(scrapeJob);
    sendScrapeJobToQueueAsync(savedJob);
    return savedJob;
  }

  @Async
  protected CompletableFuture<Void> sendScrapeBatchToQueueAsync(ScrapeBatch scrapeBatch) {
    return CompletableFuture.runAsync(() -> {
      log.info("Sending scrape batch to queue with {} jobs", 
               scrapeBatch.getScrapeJobs().size());
      
      // Process jobs in parallel
      scrapeBatch.getScrapeJobs().parallelStream()
          .forEach(this::sendScrapeJobToQueueSync);
    });
  }

  @Async
  protected CompletableFuture<Void> sendScrapeJobToQueueAsync(ScrapeJob scrapeJob) {
    return CompletableFuture.runAsync(() -> sendScrapeJobToQueueSync(scrapeJob));
  }
  
  private void sendScrapeJobToQueueSync(ScrapeJob scrapeJob) {
    log.info("Sending scrape job to queue for domain: {}", scrapeJob.getDomain());
    
    var scrapeJobRequest = scrapeJobMapper.toScrapeJobRequest(scrapeJob);

    try {
      rabbitMQProducer.sendScrapeRequest(scrapeJobRequest);
      updateJobStatusAfterQueuing(scrapeJob, true, null);
      log.info("Successfully queued scrape job for domain: {}", scrapeJob.getDomain());

    } catch (Exception e) {
      log.error("Failed to queue scrape job for domain: {} - {}",
                scrapeJob.getDomain(), e.getMessage());
      updateJobStatusAfterQueuing(scrapeJob, false, e.getMessage());
    }
  }
  
  private void updateJobStatusAfterQueuing(ScrapeJob scrapeJob, boolean success, String errorMessage) {
    if (success) {
      scrapeJob.setStatus(ScrapeJob.Status.QUEUED);
      addEventToJob(scrapeJob, ScrapeJobEvent.EventType.QUEUED, null);
    } else {
      scrapeJob.setStatus(ScrapeJob.Status.FAILED);
      addEventToJob(scrapeJob, ScrapeJobEvent.EventType.FAILED_TO_QUEUE, errorMessage);
    }
    
    scrapeJobRepository.save(scrapeJob);
  }
  
  private void addEventToJob(ScrapeJob scrapeJob, ScrapeJobEvent.EventType eventType, String details) {
    var event = ScrapeJobEvent.builder()
        .scrapeJob(scrapeJob)
        .eventType(eventType)
        .eventDetails(details)
        .build();
    
    scrapeJob.getEvents().add(event);
  }
}
