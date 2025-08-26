package com.kiosite.tasks.shared.infrastructure.persistence.memory;

import com.kiosite.tasks.shared.domain.criteria.Criteria;
import com.kiosite.tasks.shared.domain.criteria.Filter;
import com.kiosite.tasks.shared.domain.criteria.FilterOperator;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class InMemoryCriteriaConverter {
  private InMemoryCriteriaConverter() {}

  public static <T> List<T> filter(
      List<T> items, Criteria criteria, Map<String, Function<T, ?>> accessors) {
    if (criteria == null) {
      return items;
    }

    Predicate<T> predicate = buildPredicate(criteria, accessors);

    Stream<T> stream = items.stream().filter(predicate);

    Integer offset = criteria.offsetValue();
    Integer limit = criteria.limitValue();

    if (offset != null && offset > 0) {
      stream = stream.skip(offset);
    }
    if (limit != null && limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.toList();
  }

  private static <T> Predicate<T> buildPredicate(
      Criteria criteria, Map<String, Function<T, ?>> accessors) {
    if (criteria.filters() == null || criteria.filters().filters().isEmpty()) {
      return t -> true;
    }

    return criteria.filters().filters().stream()
        .map(f -> singleFilter(f, accessors))
        .reduce(t -> true, Predicate::and);
  }

  private static <T> Predicate<T> singleFilter(Filter f, Map<String, Function<T, ?>> accessors) {
    Function<T, ?> getter = accessors.get(f.field().value());
    if (getter == null) {
      throw new IllegalArgumentException("Unknown criteria field: " + f.field().value());
    }

    String rawRight = f.value().value();
    FilterOperator op = f.operator();

    return entity -> {
      Object left = getter.apply(entity);

      if (left == null) {
        return switch (op) {
          case EQUAL -> rawRight == null || "null".equalsIgnoreCase(rawRight);
          case NOT_EQUAL -> rawRight != null && !"null".equalsIgnoreCase(rawRight);
          case CONTAINS -> false;
          case NOT_CONTAINS -> true;
          case GT, LT -> false;
        };
      }

      Object right = coerce(rawRight, left.getClass());

      return switch (op) {
        case EQUAL ->
            Objects.equals(left, right)
                || (left instanceof String s && Objects.equals(s, rawRight));
        case NOT_EQUAL ->
            !(Objects.equals(left, right)
                || (left instanceof String s && Objects.equals(s, rawRight)));
        case CONTAINS -> contains(left, rawRight);
        case NOT_CONTAINS -> !contains(left, rawRight);
        case GT -> compare(left, right) > 0;
        case LT -> compare(left, right) < 0;
      };
    };
  }

  private static boolean contains(Object left, String needle) {
    if (needle == null) {
      return false;
    }
    String l = String.valueOf(left).toLowerCase(Locale.ROOT);
    return l.contains(needle.toLowerCase(Locale.ROOT));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static int compare(Object a, Object b) {
    if (a == null && b == null) return 0;
    if (a == null) return -1;
    if (b == null) return 1;
    if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
      return ((Comparable) a).compareTo(b);
    }

    return String.valueOf(a).compareTo(String.valueOf(b));
  }

  /*
   * This method converts the "right" (string) to the "left" type
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Object coerce(String raw, Class<?> target) {
    if (raw == null) return null;
    if (String.class.equals(target)) return raw;
    if (UUID.class.equals(target)) return UUID.fromString(raw);
    if (Boolean.class.equals(target) || boolean.class.equals(target)) return Boolean.valueOf(raw);
    if (Integer.class.equals(target) || int.class.equals(target)) return Integer.valueOf(raw);
    if (Long.class.equals(target) || long.class.equals(target)) return Long.valueOf(raw);
    if (Double.class.equals(target) || double.class.equals(target)) return Double.valueOf(raw);
    if (Instant.class.equals(target)) return Instant.parse(raw);
    if (LocalDate.class.equals(target)) return LocalDate.parse(raw);
    if (LocalDateTime.class.equals(target)) return LocalDateTime.parse(raw);
    if (Enum.class.isAssignableFrom(target)) {
      return Enum.valueOf((Class<Enum>) target.asSubclass(Enum.class), raw);
    }
    return raw;
  }
}
