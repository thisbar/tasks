package com.kiosite.tasks.tasks.infrastructure.persistence;

import com.kiosite.tasks.shared.domain.criteria.Criteria;
import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.shared.infrastructure.persistence.memory.InMemoryCriteriaConverter;
import com.kiosite.tasks.shared.infrastructure.persistence.memory.TaskCriteriaMapping;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.domain.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTaskRepository implements TaskRepository {
  private final Map<TaskId, Task> storage = new ConcurrentHashMap<>();

  @Override
  public void save(Task task) {
    storage.put(task.id(), task);
  }

  @Override
  public Optional<Task> search(TaskId id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<Task> matching(Criteria criteria) {
    return InMemoryCriteriaConverter.filter(
        new ArrayList<>(storage.values()), criteria, TaskCriteriaMapping.GETTERS);
  }

  @Override
  public void delete(TaskId id) {
    storage.remove(id);
  }
}
