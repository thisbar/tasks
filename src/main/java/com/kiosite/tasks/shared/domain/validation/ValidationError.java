package com.kiosite.tasks.shared.domain.validation;

public record ValidationError(String code, String message, String field) {}
