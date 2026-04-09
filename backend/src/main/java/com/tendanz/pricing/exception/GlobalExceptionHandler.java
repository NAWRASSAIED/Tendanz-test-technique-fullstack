package com.tendanz.pricing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Centralizes exception handling and provides consistent error responses.
 *
 * TODO: Implement the 3 exception handlers below.
 * Each handler should return a Map with at least: timestamp, status, error, message
 *
 * Tip: Use a consistent response format like:
 * {
 *   "timestamp": "2026-04-08T...",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "..." or "errors": { field: message }
 * }
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * TODO: Handle validation errors from @Valid request body validation.
     *
     * Requirements:
     * - Extract field-level errors from MethodArgumentNotValidException
     * - Return HTTP 400 BAD_REQUEST
     * - Include a map of field name -> error message in the response
     *
     * @param ex the validation exception
     * @return error response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * TODO: Handle IllegalArgumentException (e.g., product/zone not found).
     *
     * Requirements:
     * - Log the error
     * - Return HTTP 404 NOT_FOUND
     * - Include the exception message in the response
     *
     * @param ex the illegal argument exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    /**
     * TODO: Handle all other unexpected exceptions as a fallback.
     *
     * Requirements:
     * - Log the full exception
     * - Return HTTP 500 INTERNAL_SERVER_ERROR
     * - Return a generic error message (do NOT expose internal details)
     *
     * @param ex the exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
