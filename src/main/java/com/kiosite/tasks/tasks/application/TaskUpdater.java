package com.kiosite.tasks.tasks.application;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.tasks.domain.*;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public final class TaskUpdater {

  private final TaskRepository repository;
  private final TaskFinder taskFinder;

    public TaskUpdater(TaskRepository repository) {
        this.repository = repository;
        this.taskFinder = new TaskFinder(repository);
  }

  public Task update(
      TaskId id, TaskTitle title, TaskDescription description, Optional<TaskStatus> desiredStatus) {
    Task task = taskFinder.find(id);

    task.rename(title);
    task.changeDescription(description);

    if (desiredStatus.isPresent()) {
      TaskStatus from = task.status();
      TaskStatus to = desiredStatus.get();

      if (!Task.isAllowedTransition(from, to)) {
        throw new InvalidTransition(id, from, to);
      }

      task.transitionTo(desiredStatus.get());
    }

    repository.save(task);

    return task;
  }
}
