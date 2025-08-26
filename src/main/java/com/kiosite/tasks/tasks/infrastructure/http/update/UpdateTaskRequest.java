package com.kiosite.tasks.tasks.infrastructure.http.update;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskStatus;
import com.kiosite.tasks.tasks.domain.TaskTitle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record UpdateTaskRequest(String title, String description, String status) {

  public Validated<DomainValues> validate() {
    var voTitle = TaskTitle.from(title);
    var voDescription = TaskDescription.from(description);

    Optional<TaskStatus> vStatus = parseStatus(status);
    List<ValidationError> errors = new ArrayList<>();

    if (!voTitle.isValid()) errors.addAll(voTitle.errors());
    if (!voDescription.isValid()) errors.addAll(voDescription.errors());

    if (status != null && !status.isBlank() && vStatus.isEmpty()) {
      errors.add(new ValidationError("status.invalid", "Invalid task status", "status"));
    }

    if (!errors.isEmpty()) {
      return Validated.fail(errors);
    }
    return Validated.ok(new DomainValues(voTitle.value(), voDescription.value(), vStatus));
  }

  private static Optional<TaskStatus> parseStatus(String raw) {
    if (raw == null || raw.isBlank()) return Optional.empty();
    try {
      return Optional.of(TaskStatus.valueOf(raw.trim().toUpperCase()));
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }

  public record DomainValues(
      TaskTitle title, TaskDescription description, Optional<TaskStatus> status) {}
}
