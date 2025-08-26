package com.kiosite.tasks.tasks.application;

import com.kiosite.tasks.shared.domain.criteria.Criteria;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public final class TaskCriteriaSearcher {
  private final TaskRepository repository;

  public TaskCriteriaSearcher(TaskRepository repository) {
    this.repository = repository;
  }

  public List<Task> search(Criteria criteria) {
    return repository.matching(criteria);
  }
}
