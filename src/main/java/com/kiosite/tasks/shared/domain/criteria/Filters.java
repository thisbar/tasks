package com.kiosite.tasks.shared.domain.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public record Filters(List<Filter> filters) {

  public static Filters from(List<HashMap<String, String>> filters) {
    return new Filters(filters.stream().map(Filter::fromValues).collect(Collectors.toList()));
  }

  public static Filters none() {
    return new Filters(Collections.emptyList());
  }

  public String serialize() {
    return filters.stream().map(Filter::serialize).collect(Collectors.joining("^"));
  }
}
