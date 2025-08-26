package com.kiosite.tasks.shared.infrastructure.persistence.memory;

import com.kiosite.tasks.tasks.domain.Task;
import java.util.Map;
import java.util.function.Function;

public final class TaskCriteriaMapping {
  private TaskCriteriaMapping() {}

  public static final Map<String, Function<Task, ?>> GETTERS =
      Map.of(
          "id",
          task -> task.id().value(),
          "title",
          task -> task.title().value(),
          "description",
          task -> task.description() == null ? null : task.description().value(),
          "status",
          Task::status,
          "createdAt",
          Task::createdAt,
          "updatedAt",
          Task::updatedAt);
}
