package com.kiosite.tasks.shared.infrastructure.http.middleware;

import com.kiosite.tasks.shared.domain.DomainError;
import com.kiosite.tasks.shared.domain.validation.ValidationError;
import com.kiosite.tasks.shared.domain.validation.ValidationException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ApiErrorHandler {

  // Validation error bag (domain)
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<List<ValidationError>> onValidation(ValidationException ex) {
    return ResponseEntity.badRequest().body(ex.errors());
  }

  // Domain errors
  @ExceptionHandler(DomainError.class)
  public ResponseEntity<?> onDomain(DomainError ex) {
    HttpStatus status = mapStatusByCode(ex.errorCode());
    return ResponseEntity.status(status)
        .body(Map.of("code", ex.errorCode(), "message", ex.errorMessage()));
  }

  // Malformed JSON
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> onBadJson(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(Map.of("code", "bad_request", "message", "Malformed JSON request"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> onUnexpected(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("code", "internal_error", "message", "Unexpected error"));
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<?> onNoHandler(NoHandlerFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("code", "not_found", "message", "Resource not found"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> onMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(Map.of("code", "method_not_allowed", "message", "Method not allowed"));
  }

  private static HttpStatus mapStatusByCode(String code) {
    if (code == null) return HttpStatus.BAD_REQUEST;
    String c = code.toLowerCase(Locale.ROOT);

    if (c.endsWith(".not_found") || c.contains("not_found")) return HttpStatus.NOT_FOUND;

    return HttpStatus.BAD_REQUEST;
  }
}
