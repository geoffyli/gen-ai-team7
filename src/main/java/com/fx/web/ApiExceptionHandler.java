package com.fx.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global error handling — a customer never sees a Java stack trace.
 * BASELINE: bad input (an IllegalArgumentException) -> 400 with a clean JSON message.
 *
 * We deliberately do NOT add a catch-all {@code @ExceptionHandler(Exception.class)}: that
 * would swallow Spring's own web exceptions (e.g. NoResourceFoundException) and turn honest
 * 404s into 500s. Unexpected errors are handled by Spring Boot's default error path, which —
 * with server.error.include-stacktrace=never / include-message=never in application.properties
 * — already returns a clean response with no stack trace to the browser.
 *
 * When you build the "validation & error handling" requirement, EXTEND this class:
 * add a 404 for an unknown currency pair, field-level validation messages, etc.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage() == null ? "bad request" : ex.getMessage());
    }
}
