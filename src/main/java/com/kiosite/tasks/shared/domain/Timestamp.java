package com.kiosite.tasks.shared.domain;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public record Timestamp(Instant value) {
  public Timestamp(String value) {
    this(parse(value));
  }

  public static Timestamp now() {
    return new Timestamp(Instant.now());
  }

  private static Instant parse(String value) {
    try {
      return Instant.parse(value);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid timestamp: " + value, e);
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
