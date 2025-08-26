package com.kiosite.tasks.tasks.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.domain.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
final class TaskUpdaterTest {

  @Mock private TaskRepository repository;

  private TaskUpdater updater;

  @BeforeEach
  void setUp() {
    updater = new TaskUpdater(repository);
  }

  @Test
  void should_update_title_and_description_without_status_change() {
    TaskTitle oldTitle = TaskTitle.from("Old title").orElseThrow();
    TaskDescription oldDesc = TaskDescription.from("Old description").orElseThrow();
    Task existing = Task.create(oldTitle, oldDesc);

    TaskId id = existing.id();
    when(repository.search(id)).thenReturn(Optional.of(existing));

    // When
    TaskTitle newTitle = TaskTitle.from("New title").orElseThrow();
    TaskDescription newDesc = TaskDescription.from("New description").orElseThrow();
    Task result = updater.update(id, newTitle, newDesc, Optional.empty());

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(repository);

    Task saved = captor.getValue();
    assertEquals(newTitle, saved.title());
    assertEquals(newDesc, saved.description());
    assertEquals(existing.status(), saved.status(), "status should not change");
    assertEquals(saved, result);
  }

  @Test
  void should_update_status_from_pending_to_in_progress() {
    // Given
    Task existing =
        Task.create(TaskTitle.from("t").orElseThrow(), TaskDescription.from("d").orElseThrow());
    TaskId id = existing.id();
    when(repository.search(id)).thenReturn(Optional.of(existing));

    // When
    Task updated =
        updater.update(
            id, existing.title(), existing.description(), Optional.of(TaskStatus.IN_PROGRESS));

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    Task saved = captor.getValue();

    assertEquals(TaskStatus.IN_PROGRESS, saved.status());
    assertEquals(saved, updated);
  }

  @Test
  void should_fail_transition_from_in_progress_directly_to_done() {
    // Given
    Task task =
        Task.create(TaskTitle.from("t").orElseThrow(), TaskDescription.from("d").orElseThrow());
    TaskId id = task.id();
    when(repository.search(id)).thenReturn(Optional.of(task));

    // When + Then
    assertThrows(
        InvalidTransition.class,
        () -> updater.update(id, task.title(), task.description(), Optional.of(TaskStatus.DONE)));
    verify(repository, never()).save(any());
  }

  @Test
  void should_allow_reverse_transition_from_done_to_in_progress() {
    // Given: PENDING -> IN_PROGRESS -> READY_FOR_REVIEW -> DONE
    Task task =
        Task.create(TaskTitle.from("t").orElseThrow(), TaskDescription.from("d").orElseThrow());

    task.transitionTo(TaskStatus.IN_PROGRESS);
    task.transitionTo(TaskStatus.READY_FOR_REVIEW);
    task.transitionTo(TaskStatus.DONE);

    TaskId id = task.id();
    when(repository.search(id)).thenReturn(Optional.of(task));

    // When
    Task updated =
        updater.update(id, task.title(), task.description(), Optional.of(TaskStatus.IN_PROGRESS));

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    Task saved = captor.getValue();

    assertEquals(TaskStatus.IN_PROGRESS, saved.status());
    assertEquals(saved, updated);
  }

  @Test
  void should_throw_not_found_when_task_does_not_exist() {
    // Given
    TaskId id = TaskId.random();
    when(repository.search(id)).thenReturn(Optional.empty());

    // When + Then
    assertThrows(
        TaskNotFound.class,
        () ->
            updater.update(
                id,
                TaskTitle.from("t").orElseThrow(),
                TaskDescription.from("d").orElseThrow(),
                Optional.empty()));
    verify(repository, never()).save(any());
  }

  @Test
  void should_normalize_blank_description_to_null_on_update() {
    // Given
    Task existing =
        Task.create(
            TaskTitle.from("title").orElseThrow(), TaskDescription.from("desc").orElseThrow());
    TaskId id = existing.id();
    when(repository.search(id)).thenReturn(Optional.of(existing));

    TaskDescription blank = TaskDescription.from("   ").orElseThrow();

    // When
    updater.update(id, existing.title(), blank, Optional.empty());

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    Task saved = captor.getValue();

    assertNull(saved.description().value(), "description should be null when blank");
  }

  @Test
  void should_fail_when_title_is_invalid() {
    // Given
    TaskId id = TaskId.random();
    TaskDescription desc = TaskDescription.from("d").orElseThrow();

    // When + Then
    assertThrows(
        ValidationException.class,
        () -> updater.update(id, TaskTitle.from("   ").orElseThrow(), desc, Optional.empty()));

    verifyNoInteractions(repository);
  }
}
