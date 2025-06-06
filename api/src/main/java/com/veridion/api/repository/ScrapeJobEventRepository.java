package com.veridion.api.repository;

import com.veridion.api.domain.ScrapeJobEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapeJobEventRepository extends JpaRepository<ScrapeJobEvent, Long> {

}
