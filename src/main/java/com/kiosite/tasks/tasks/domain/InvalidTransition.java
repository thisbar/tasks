package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.DomainError;
import com.kiosite.tasks.shared.domain.identifiers.TaskId;

public final class InvalidTransition extends DomainError {
  private final TaskId id;
  private final TaskStatus from;
  private final TaskStatus to;

  public InvalidTransition(TaskId id, TaskStatus from, TaskStatus to) {
    super(
        "invalid_task_status_transition",
        "Cannot transition task <%s> from %s to %s".formatted(id.value(), from, to));
    this.id = id;
    this.from = from;
    this.to = to;
  }

  public TaskId id() {
    return id;
  }

  public TaskStatus from() {
    return from;
  }

  public TaskStatus to() {
    return to;
  }
}
