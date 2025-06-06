package com.veridion.api.rest.representation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProblemDetailsRepresentation {

  private final String title;

  private final List<Object> errors = new ArrayList<>();

  public ProblemDetailsRepresentation(String title) {
    this.title = title;
  }

  public ProblemDetailsRepresentation(String title, List<Object> errors) {
    this.title = title;
    this.errors.addAll(errors);
  }

}
