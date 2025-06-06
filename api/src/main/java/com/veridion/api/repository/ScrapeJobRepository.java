package com.veridion.api.repository;

import com.veridion.api.domain.ScrapeJob;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapeJobRepository extends JpaRepository<ScrapeJob, Long> {

}
