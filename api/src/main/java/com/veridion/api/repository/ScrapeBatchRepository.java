package com.veridion.api.repository;

import com.veridion.api.domain.ScrapeBatch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapeBatchRepository extends JpaRepository<ScrapeBatch, Long> {

}
