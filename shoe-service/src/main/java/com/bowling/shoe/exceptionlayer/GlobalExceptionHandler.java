package com.bowling.shoe.exceptionlayer;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShoeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleShoeNotFound(ShoeNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumConversion(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String fullMessage = ex.getMessage();

        if (fullMessage != null && fullMessage.contains("ShoeSize")) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid shoe size. Must be one of: 5, 6, 7, 8, 9, 10, 11, 12.");
            error.put("timestamp", LocalDateTime.now().toString());
            error.put("path", request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        if (fullMessage != null && fullMessage.contains("ShoeStatus")) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid shoe status. Must be one of: AVAILABLE, IN_USE.");
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


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllOtherExceptions(Exception ex, HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Internal server error");
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}