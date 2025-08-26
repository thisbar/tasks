package com.kiosite.tasks.tasks.infrastructure.http.find;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.application.TaskFinder;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.infrastructure.http.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public final class GetTaskByIdController {

  private final TaskFinder finder;

  public GetTaskByIdController(TaskFinder finder) {
    this.finder = finder;
  }

  @GetMapping("/tasks/{id}")
  public ResponseEntity<?> handle(@PathVariable String id) throws Exception {
    var voId = TaskId.from(id);
    if (!voId.isValid()) {
      throw new ValidationException(voId.errors());
    }

    Task task = finder.find(voId.value());

    return ResponseEntity.ok(TaskResponse.from(task));
  }
}
