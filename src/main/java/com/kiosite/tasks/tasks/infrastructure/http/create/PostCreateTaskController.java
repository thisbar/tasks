package com.kiosite.tasks.tasks.infrastructure.http.create;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import com.kiosite.tasks.tasks.application.TaskCreator;
import com.kiosite.tasks.tasks.domain.Task;
import com.kiosite.tasks.tasks.infrastructure.http.TaskResponse;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public final class PostCreateTaskController {

  private final TaskCreator creator;

  public PostCreateTaskController(TaskCreator creator) {
    this.creator = creator;
  }

  @PostMapping("/tasks")
  public ResponseEntity<?> handle(@RequestBody CreateTaskRequest body) {
    var validated = body.validate();
    guardValuesAreValid(validated);

    var validatedValueObjects = validated.value();
    Task task = creator.create(validatedValueObjects.title(), validatedValueObjects.description());

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(task.id())
            .toUri();

    return ResponseEntity.created(location).body(TaskResponse.from(task));
  }

  private static void guardValuesAreValid(Validated<CreateTaskRequest.DomainValues> validated) {
    if (!validated.isValid()) {
      throw new ValidationException(validated.errors());
    }
  }
}
