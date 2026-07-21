package com.fx.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global error handling — a customer never sees a Java stack trace.
 * BASELINE: bad input (an IllegalArgumentException) -> 400 with a clean JSON message.
 * An unknown lookup (a NoSuchElementException, e.g. an unknown currency pair) -> 404.
 * VALIDATION: a missing request param, a non-numeric value, or a malformed JSON body -> 400.
 *
 * We deliberately do NOT add a catch-all {@code @ExceptionHandler(Exception.class)}: that
 * would swallow Spring's own web exceptions (e.g. NoResourceFoundException) and turn honest
 * 404s into 500s. Unexpected errors are handled by Spring Boot's default error path, which —
 * with server.error.include-stacktrace=never / include-message=never in application.properties
 * — already returns a clean response with no stack trace to the browser.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage() == null ? "bad request" : ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage() == null ? "not found" : ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> missingParam(MissingServletRequestParameterException ex) {
        return Map.of("error", "missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badParamType(MethodArgumentTypeMismatchException ex) {
        String expected = ex.getRequiredType() == null ? "a valid value" : "a valid " + ex.getRequiredType().getSimpleName();
        return Map.of("error", ex.getName() + " must be " + expected);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> malformedBody(HttpMessageNotReadableException ex) {
        return Map.of("error", "malformed request body");
    }
}
