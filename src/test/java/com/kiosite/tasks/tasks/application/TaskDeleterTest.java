package com.kiosite.tasks.tasks.application;

import static org.mockito.Mockito.*;

import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import com.kiosite.tasks.tasks.domain.TaskTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
final class TaskDeleterTest {

  @Mock private TaskRepository repository;

  private TaskCreator creator;
  private TaskDeleter deleter;

  @BeforeEach
  void setUp() {
    creator = new TaskCreator(repository);
    deleter = new TaskDeleter(repository);
  }

  @Test
  void should_delete_task_by_id() {
    // Given
    TaskTitle title = new TaskTitle("title");
    TaskDescription description = new TaskDescription("description");
    Task task = creator.create(title, description);

    // When
    deleter.delete(task.id());

    // Then
    verify(repository, times(1)).save(task);
    verify(repository, times(1)).delete(task.id());
    verifyNoMoreInteractions(repository);
  }
}
