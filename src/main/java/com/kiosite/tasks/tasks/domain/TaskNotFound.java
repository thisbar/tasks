package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.DomainError;
import com.kiosite.tasks.shared.domain.identifiers.TaskId;

public final class TaskNotFound extends DomainError {
  public TaskNotFound(TaskId id) {
    super("task_not_found", String.format("The task <%s> doesn't exist", id.value()));
  }
}
