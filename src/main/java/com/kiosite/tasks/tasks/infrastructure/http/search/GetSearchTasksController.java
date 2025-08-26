package com.kiosite.tasks.tasks.infrastructure.http.search;

import com.kiosite.tasks.shared.domain.criteria.*;
import com.kiosite.tasks.tasks.application.TaskCriteriaSearcher;
import com.kiosite.tasks.tasks.infrastructure.http.TaskResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class GetSearchTasksController {
  private final TaskCriteriaSearcher searcher;

  public GetSearchTasksController(TaskCriteriaSearcher searcher) {
    this.searcher = searcher;
  }

  @GetMapping("/tasks")
  public ResponseEntity<?> handle(
      @RequestParam Optional<String> status,
      @RequestParam Optional<Integer> limit,
      @RequestParam Optional<Integer> offset) {
    Criteria criteria = generateCriteria(status, limit, offset);

    var tasks = searcher.search(criteria);
    var body = tasks.stream().map(TaskResponse::from).toList();
    return ResponseEntity.ok(body);
  }

  private static Criteria generateCriteria(
      Optional<String> status, Optional<Integer> limit, Optional<Integer> offset) {
    List<Filter> fs = new ArrayList<>();

    status
        .filter(s -> !s.isBlank())
        .ifPresent(
            s ->
                fs.add(
                    new Filter(
                        new FilterField("status"), FilterOperator.EQUAL, new FilterValue(s))));

    return new Criteria(new Filters(fs), limit, offset);
  }
}
