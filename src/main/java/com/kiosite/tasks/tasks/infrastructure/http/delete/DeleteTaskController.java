package com.kiosite.tasks.tasks.infrastructure.http.delete;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.application.TaskDeleter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public final class DeleteTaskController {

  private final TaskDeleter deleter;

  public DeleteTaskController(TaskDeleter deleter) {
    this.deleter = deleter;
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> handle(@PathVariable String id) {
    var voId = TaskId.from(id);

    if (!voId.isValid()) {
      throw new ValidationException(voId.errors());
    }

    deleter.delete(voId.value());
    return ResponseEntity.noContent().build();
  }
}
