package com.kiosite.tasks.tasks.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.tasks.domain.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
final class TaskFinderTest {

  @Mock private TaskRepository repository;

  private TaskFinder finder;

  @BeforeEach
  void setUp() {
    finder = new TaskFinder(repository);
  }

  @Test
  void should_return_the_task_when_it_exists() throws Exception {
    // Given
    TaskId id = TaskId.random();
    Task expected = Task.create(new TaskTitle("title"), new TaskDescription("description"));

    when(repository.search(id)).thenReturn(Optional.of(expected));

    // When
    Task found = finder.find(id);

    // Then
    assertEquals(expected, found, "finder must return the task provided by repository");
    verify(repository, times(1)).search(id);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void should_throw_TaskNotFound_when_task_does_not_exist() {
    // Given
    TaskId id = TaskId.random();
    when(repository.search(id)).thenReturn(Optional.empty());

    // When + Then
    assertThrows(TaskNotFound.class, () -> finder.find(id));
    verify(repository, times(1)).search(id);
    verifyNoMoreInteractions(repository);
  }
}
