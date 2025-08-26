package com.kiosite.tasks.shared.domain.identifiers;

import com.kiosite.tasks.shared.domain.Identifier;
import com.kiosite.tasks.shared.domain.validation.Validated;

public final class TaskId extends Identifier {
  public TaskId(String value) {
    super(value);
  }

  public static Validated<TaskId> from(String value) {
    var v = validateUuid(value, "id", "task_id.invalid");
    return v.isValid() ? Validated.ok(new TaskId(v.value())) : Validated.fail(v.errors());
  }

  public static TaskId random() {
    return new TaskId(randomValue());
  }
}
