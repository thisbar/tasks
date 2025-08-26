package com.kiosite.tasks.shared.domain.criteria;

import java.util.Optional;

public record Criteria(Filters filters, Optional<Integer> limit, Optional<Integer> offset) {
  private static final int DEFAULT_LIMIT = 25;
  private static final int DEFAULT_OFFSET = 0;

  public Criteria(Filters filters, Optional<Integer> limit, Optional<Integer> offset) {
    this.filters = filters != null ? filters : new Filters(java.util.List.of());

    int lim = (limit != null && limit.isPresent()) ? limit.get() : DEFAULT_LIMIT;
    int off = (offset != null && offset.isPresent()) ? offset.get() : DEFAULT_OFFSET;

    if (lim <= 0) lim = DEFAULT_LIMIT;
    if (off < 0) off = DEFAULT_OFFSET;

    this.limit = Optional.of(lim);
    this.offset = Optional.of(off);
  }

  public Criteria(Filters filters) {
    this(filters, Optional.of(DEFAULT_LIMIT), Optional.of(DEFAULT_OFFSET));
  }

  public boolean hasFilters() {
    return !filters.filters().isEmpty();
  }

  public int limitValue() {
    return limit.orElse(DEFAULT_LIMIT);
  }

  public int offsetValue() {
    return offset.orElse(DEFAULT_OFFSET);
  }

  public String serialize() {
    return String.format("%s~~%s~~%s~~%s", filters.serialize(), offsetValue(), limitValue());
  }
}
