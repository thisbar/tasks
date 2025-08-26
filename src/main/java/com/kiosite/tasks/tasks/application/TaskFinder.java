package com.kiosite.tasks.tasks.application;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskNotFound;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import com.kiosite.tasks.tasks.domain.TaskSearcher;
import org.springframework.stereotype.Service;

@Service
public final class TaskFinder {
  private final TaskSearcher taskSearcher;

  TaskFinder(TaskRepository repository) {
    this.taskSearcher = new TaskSearcher(repository);
  }

  public Task find(TaskId id) {
    return taskSearcher.search(id).orElseThrow(() -> new TaskNotFound(id));
  }
}
