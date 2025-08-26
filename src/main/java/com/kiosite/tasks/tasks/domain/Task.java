package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.Timestamp;
import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import java.util.Objects;

public final class Task {
  private final TaskId id;
  private TaskTitle title;
  private TaskDescription description;
  private TaskStatus status;
  private final Timestamp createdAt;
  private Timestamp updatedAt;

  private Task(
      TaskId id,
      TaskTitle title,
      TaskDescription description,
      TaskStatus status,
      Timestamp createdAt,
      Timestamp updatedAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Task create(TaskTitle title, TaskDescription description) {
    var now = Timestamp.now();
    return new Task(TaskId.random(), title, description, TaskStatus.PENDING, now, now);
  }

  public void rename(TaskTitle newTitle) {
    this.title = Objects.requireNonNull(newTitle);
    touch();
  }

  public void changeDescription(TaskDescription newDescription) {
    this.description = newDescription;
    touch();
  }

  public void transitionTo(TaskStatus to) {
    Objects.requireNonNull(to);
    if (this.status == to) return;
    if (!isAllowedTransition(this.status, to)) {
      throw new InvalidTransition(this.id, this.status, to);
    }

    this.status = to;
    touch();
  }

  public static boolean isAllowedTransition(TaskStatus from, TaskStatus to) {
    return switch (from) {
      case PENDING -> to == TaskStatus.IN_PROGRESS;
      case IN_PROGRESS -> to == TaskStatus.READY_FOR_REVIEW;
      case READY_FOR_REVIEW -> to == TaskStatus.DONE;
      case DONE -> to == TaskStatus.IN_PROGRESS;
    };
  }

  private void touch() {
    this.updatedAt = Timestamp.now();
  }

  public TaskId id() {
    return id;
  }

  public TaskTitle title() {
    return title;
  }

  public TaskDescription description() {
    return description;
  }

  public TaskStatus status() {
    return status;
  }

  public Timestamp createdAt() {
    return createdAt;
  }

  public Timestamp updatedAt() {
    return updatedAt;
  }
}
