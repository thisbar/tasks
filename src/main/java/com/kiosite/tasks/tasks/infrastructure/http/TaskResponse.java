package com.kiosite.tasks.tasks.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kiosite.tasks.tasks.domain.Task;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
    String id,
    String title,
    String description,
    String status,
    Instant createdAt,
    Instant updatedAt) {
  public static TaskResponse from(Task task) {
    return new TaskResponse(
        task.id().value(),
        task.title().value(),
        task.description().value(),
        task.status().name(),
        task.createdAt().value(),
        task.updatedAt().value());
  }
}
