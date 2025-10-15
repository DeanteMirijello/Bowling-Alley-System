package com.bowling.bowlingball.exceptionlayer;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BowlingBallNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(BowlingBallNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Internal server error");
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumConversion(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String fullMessage = ex.getMessage();

        if (fullMessage != null && fullMessage.contains("BallSize")) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid ball size. Must be one of: SIX, EIGHT, TEN, TWELVE, FOURTEEN, SIXTEEN.");
            error.put("timestamp", LocalDateTime.now().toString());
            error.put("path", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("message", "Invalid request body.");
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request data");

        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }



}

