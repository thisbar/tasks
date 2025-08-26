package com.kiosite.tasks.shared.domain.validation;

import java.util.List;

public record Validated<T>(T value, List<ValidationError> errors) {
  public static <T> Validated<T> ok(T v) {
    return new Validated<>(v, List.of());
  }

  public static <T> Validated<T> fail(List<ValidationError> e) {
    return new Validated<>(null, List.copyOf(e));
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  public T orElseThrow() {
    if (isValid()) return value;
    throw new ValidationException(errors);
  }
}
