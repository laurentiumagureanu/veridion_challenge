package com.veridion.api.config;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(basePackages = "com.veridion.api.repository")
public class ElasticsearchConfiguration {

}
