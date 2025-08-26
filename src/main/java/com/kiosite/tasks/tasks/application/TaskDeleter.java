package com.kiosite.tasks.tasks.application;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public final class TaskDeleter {
  private final TaskRepository repository;

  TaskDeleter(TaskRepository repository) {
    this.repository = repository;
  }

  public void delete(TaskId id) {
    repository.delete(id);
  }
}
