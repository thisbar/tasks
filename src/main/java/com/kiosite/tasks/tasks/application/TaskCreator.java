package com.kiosite.tasks.tasks.application;

import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import com.kiosite.tasks.tasks.domain.TaskTitle;
import org.springframework.stereotype.Service;

@Service
public final class TaskCreator {
  private final TaskRepository repository;

  TaskCreator(TaskRepository repository) {
    this.repository = repository;
  }

  public Task create(TaskTitle title, TaskDescription description) {
    Task task = Task.create(title, description);

    repository.save(task);

    return task;
  }
}
