package com.veridion.api.rest.exception;

import com.veridion.api.rest.representation.ProblemDetailsRepresentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NotFoundExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ProblemDetailsRepresentation> handleTenantNotFoundException(NotFoundException exception) {
    ProblemDetailsRepresentation problemDetails = new ProblemDetailsRepresentation(exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetails);
  }

}
