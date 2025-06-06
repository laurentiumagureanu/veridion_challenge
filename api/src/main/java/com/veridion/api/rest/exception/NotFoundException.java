package com.veridion.api.rest.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException(Long id, String entityName) {
    super("Entity not found: " + entityName + " with ID " + id);
  }
}
