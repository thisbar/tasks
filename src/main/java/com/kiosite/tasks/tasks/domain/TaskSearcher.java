package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import java.util.Optional;

public final class TaskSearcher {
  private final TaskRepository repository;

  public TaskSearcher(TaskRepository repository) {
    this.repository = repository;
  }

  public Optional<Task> search(TaskId id) {
    return this.repository.search(id);
  }
}
