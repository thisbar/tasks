package com.kiosite.tasks.tasks.infrastructure.http.update;

import com.kiosite.tasks.shared.domain.identifiers.TaskId;
import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.application.TaskUpdater;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.infrastructure.http.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public final class PutUpdateTaskController {

  private final TaskUpdater updater;

  public PutUpdateTaskController(TaskUpdater updater) {
    this.updater = updater;
  }

  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> handle(@PathVariable String id, @RequestBody UpdateTaskRequest body) {
    var validated = body.validate();
    guardValuesAreValid(validated);

    var validatedValueObjects = validated.value();

    TaskId taskId = new TaskId(id);

    Task updated =
        updater.update(
            taskId,
            validatedValueObjects.title(),
            validatedValueObjects.description(),
            validatedValueObjects.status());
    return ResponseEntity.ok(TaskResponse.from(updated));
  }

  private static void guardValuesAreValid(Validated<UpdateTaskRequest.DomainValues> validated) {
    if (!validated.isValid()) {
      throw new ValidationException(validated.errors());
    }
  }
}
