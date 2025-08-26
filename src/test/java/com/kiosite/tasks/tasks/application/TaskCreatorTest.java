package com.kiosite.tasks.tasks.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import com.kiosite.tasks.tasks.domain.TaskTitle;
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
final class TaskCreatorTest {
  @Mock private TaskRepository repository;

  private TaskCreator creator;

  @BeforeEach
  void setUp() {
    creator = new TaskCreator(repository);
  }

  @Test
  void should_save_a_valid_task() {
    // Given
    TaskTitle title = TaskTitle.from("Task title example").orElseThrow();
    TaskDescription description = TaskDescription.from("Task description").orElseThrow();

    // When
    creator.create(title, description);

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(repository);

    Task saved = captor.getValue();
    assertEquals(title, saved.title(), "given title doesn't match the expected");
    assertEquals(description, saved.description(), "given description doesn't match the expected");
  }

  @Test
  void should_not_save_when_title_is_blank() {
    // Given
    TaskDescription description = new TaskDescription("description");

    // When + Then
    assertThrows(
        ValidationException.class,
        () -> creator.create(TaskTitle.from("   ").orElseThrow(), description));

    verifyNoInteractions(repository);
  }

  @Test
  void should_not_save_when_title_length_is_higher() {
    // Given
    TaskDescription description = new TaskDescription("description");

    // When + Then
    String title =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et";
    assertThrows(
        ValidationException.class,
        () -> creator.create(TaskTitle.from(title).orElseThrow(), description));

    verifyNoInteractions(repository);
  }

  @Test
  void should_normalize_blank_description_to_null() {
    // Given
    TaskTitle title = new TaskTitle("title");
    TaskDescription blankDescription = TaskDescription.from("   ").orElseThrow();

    // When
    creator.create(title, blankDescription);

    // Then
    ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
    verify(repository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(repository);

    Task saved = captor.getValue();
    assertEquals(title, saved.title(), "given title doesn't match the expected");
    assertNull(saved.description().value(), "description should be null when blank");
  }
}
