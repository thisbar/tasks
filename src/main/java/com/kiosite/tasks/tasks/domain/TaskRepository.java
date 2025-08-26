package com.kiosite.tasks.tasks.domain;

import com.kiosite.tasks.shared.domain.criteria.Criteria;
import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
  public void save(Task task);

  public Optional<Task> search(TaskId id);

  public List<Task> matching(Criteria criteria);

  public void delete(TaskId id);
}
