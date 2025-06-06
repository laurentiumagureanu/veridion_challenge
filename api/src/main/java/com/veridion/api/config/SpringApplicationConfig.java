package com.veridion.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class SpringApplicationConfig {

  /**
   * Primary ObjectMapper configuration
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    var mapper = new ObjectMapper();
    
    // Configure serialization using method chaining
    configureSerializationFeatures(mapper);
    configureDeserializationFeatures(mapper);
    registerModules(mapper);
    configureHalSupport(mapper);
    
    return mapper;
  }

  /**
   * Enhanced JSON message converter with dependency injection
   */
  @Bean
  public Jackson2JsonMessageConverter jsonConverter(ObjectMapper objectMapper) {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  /**
   * RabbitMQ template configuration with enhanced error handling
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                     Jackson2JsonMessageConverter jsonConverter) {
    var template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonConverter);
    
    // Configure retry and error handling
    template.setMandatory(true);
    template.setRetryTemplate(createRetryTemplate());
    
    return template;
  }
  
  /**
   * Enhanced async task executor for better performance
   */
  @Bean("taskExecutor")
  public AsyncTaskExecutor taskExecutor() {
    // Using cached thread pool with better performance characteristics
    var executor = Executors.newCachedThreadPool(r -> {
      var thread = new Thread(r);
      thread.setName("async-task-");
      thread.setDaemon(true);
      return thread;
    });
    
    return new TaskExecutorAdapter(executor);
  }
  
  // Private helper methods
  
  private void configureSerializationFeatures(ObjectMapper mapper) {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }
  
  private void configureDeserializationFeatures(ObjectMapper mapper) {
    mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
  }
  
  private void registerModules(ObjectMapper mapper) {
    // Register modules using var and method chaining
    var javaTimeModule = new JavaTimeModule();
    var halModule = new Jackson2HalModule();
    
    mapper.registerModule(javaTimeModule);
    mapper.registerModule(halModule);
  }
  
  private void configureHalSupport(ObjectMapper mapper) {
    var halHandlerInstantiator = new Jackson2HalModule.HalHandlerInstantiator(
        new AnnotationLinkRelationProvider(),
        CurieProvider.NONE,
        MessageResolver.DEFAULTS_ONLY
    );
    
    mapper.setHandlerInstantiator(halHandlerInstantiator);
  }
  
  private RetryTemplate createRetryTemplate() {
    var retryTemplate = new RetryTemplate();
    
    // Configure retry policy using modern Java patterns
    var retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3);
    retryTemplate.setRetryPolicy(retryPolicy);
    
    // Configure backoff policy
    var backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(1000);
    backOffPolicy.setMultiplier(2.0);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    
    return retryTemplate;
  }
}
