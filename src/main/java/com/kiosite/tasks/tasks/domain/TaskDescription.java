package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import java.util.ArrayList;

public record TaskDescription(String value) {
  public static final int MAX_LENGTH = 500;

  public TaskDescription {
    if (value != null && value.isBlank()) value = null;
  }

  public static Validated<TaskDescription> from(String value) {
    var errors = new ArrayList<ValidationError>();
    String normalized = null;

    if (value == null || value.isBlank()) {
      normalized = null;
    } else if (value.length() > MAX_LENGTH) {
      errors.add(
          new ValidationError(
              "description.too_long", "Max %d characters".formatted(MAX_LENGTH), "description"));
    } else {
      normalized = value.trim();
    }

    return errors.isEmpty()
        ? Validated.ok(new TaskDescription(normalized))
        : Validated.fail(errors);
  }
}
