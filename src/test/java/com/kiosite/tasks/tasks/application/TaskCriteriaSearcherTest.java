package com.kiosite.tasks.tasks.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.kiosite.tasks.shared.domain.criteria.Criteria;
import com.kiosite.tasks.shared.domain.criteria.Filters;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import com.kiosite.tasks.tasks.domain.TaskTitle;
import java.util.List;
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
final class TaskCriteriaSearcherTest {

  @Mock private TaskRepository repository;

  private TaskCriteriaSearcher searcher;

  @BeforeEach
  void setUp() {
    searcher = new TaskCriteriaSearcher(repository);
  }

  @Test
  void should_delegate_matching_and_return_results() {
    // Given
    Criteria criteria = new Criteria(new Filters(List.of()), Optional.of(10), Optional.of(0));
    Task t1 = Task.create(new TaskTitle("A"), new TaskDescription("d1"));
    Task t2 = Task.create(new TaskTitle("B"), new TaskDescription("d2"));
    List<Task> expected = List.of(t1, t2);

    when(repository.matching(criteria)).thenReturn(expected);

    // When
    List<Task> result = searcher.search(criteria);

    // Then
    assertEquals(expected, result);
    verify(repository, times(1)).matching(criteria);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void should_return_empty_list_when_no_results() {
    // Given
    Criteria criteria = new Criteria(new Filters(List.of()));
    when(repository.matching(criteria)).thenReturn(List.of());

    // When
    List<Task> result = searcher.search(criteria);

    // Then
    assertTrue(result.isEmpty());
    verify(repository, times(1)).matching(criteria);
    verifyNoMoreInteractions(repository);
  }
}
