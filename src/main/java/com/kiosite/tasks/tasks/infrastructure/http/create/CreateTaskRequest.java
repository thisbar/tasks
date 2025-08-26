package com.kiosite.tasks.tasks.infrastructure.http.create;

import com.kiosite.tasks.shared.domain.validation.Validated;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import com.kiosite.tasks.tasks.domain.TaskDescription;
import com.kiosite.tasks.tasks.domain.TaskTitle;
import java.util.ArrayList;
import java.util.List;

public record CreateTaskRequest(String title, String description) {
  public Validated<DomainValues> validate() {
    var voTitle = TaskTitle.from(title);
    var voDescription = TaskDescription.from(description);

    if (voTitle.isValid() && voDescription.isValid()) {
      return Validated.ok(new DomainValues(voTitle.value(), voDescription.value()));
    }

    List<ValidationError> all = new ArrayList<>();
    if (!voTitle.isValid()) all.addAll(voTitle.errors());
    if (!voDescription.isValid()) all.addAll(voDescription.errors());
    return Validated.fail(all);
  }

  public record DomainValues(TaskTitle title, TaskDescription description) {}
}
