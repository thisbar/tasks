package com.kiosite.tasks.shared.domain;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Identifier {
  protected final String value;

  public Identifier(String value) {
    ensureValidUuid(value);
    this.value = value;
  }

  protected Identifier() {
    this.value = null;
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Identifier that = (Identifier) o;
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  private void ensureValidUuid(String value) throws IllegalArgumentException {
    UUID.fromString(value);
  }

  protected static String randomValue() {
    return UUID.randomUUID().toString();
  }

  protected static Validated<String> validateUuid(String value, String field, String code) {
    List<ValidationError> errors = new ArrayList<>();

    if (value == null || value.isBlank()) {
      errors.add(new ValidationError(code, "Value is required and must be a UUID", field));
    } else {
      try {
        UUID.fromString(value);
      } catch (IllegalArgumentException ex) {
        errors.add(new ValidationError(code, "Invalid UUID", field));
      }
    }

    return errors.isEmpty() ? Validated.ok(value) : Validated.fail(errors);
  }
}
