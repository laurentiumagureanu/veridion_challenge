package com.veridion.api.repository;

import com.veridion.api.domain.CompanyNamesDatasource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyNamesDatasourceRepository extends JpaRepository<CompanyNamesDatasource, String> {

}
