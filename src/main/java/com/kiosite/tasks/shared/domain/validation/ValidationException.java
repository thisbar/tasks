package com.kiosite.tasks.shared.domain.validation;

import java.util.List;

public final class ValidationException extends RuntimeException {
  private final List<ValidationError> errors;

  public ValidationException(List<ValidationError> errors) {
    super("Validation failed");
    this.errors = List.copyOf(errors);
  }

  public List<ValidationError> errors() {
    return errors;
  }
}
