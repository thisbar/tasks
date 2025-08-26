package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;

public record TaskTitle(String value) {
  public static final Byte MAX_LENGHT = 100;

  public static Validated<TaskTitle> from(String value) {
    List<ValidationError> errors = new ArrayList<>();

    if (value == null || value.isBlank()) {
      errors.add(new ValidationError("title.blank", "Title is required", "title"));
    } else if (value.length() > MAX_LENGHT) {
      errors.add(
          new ValidationError(
              "title.too_long", "Max %d characters".formatted(MAX_LENGHT), "title"));
    }

    return errors.isEmpty() ? Validated.ok(new TaskTitle(value.trim())) : Validated.fail(errors);
  }
}
