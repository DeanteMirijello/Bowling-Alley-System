package com.bowling.apigateway.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
    ErrorResponse error = ErrorResponse.builder()
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<ErrorResponse> handleUnprocessableEntity(RuntimeException ex, HttpServletRequest request) {
    ErrorResponse error = ErrorResponse.builder()
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
    String message = extractEnumErrorMessage(ex);
    ErrorResponse error = ErrorResponse.builder()
            .message(message)
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    ErrorResponse error = ErrorResponse.builder()
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private String extractEnumErrorMessage(HttpMessageNotReadableException ex) {
    String fullMessage = ex.getMessage();

    if (fullMessage != null && fullMessage.contains("BallSize")) {
      return "Invalid ball size. Must be one of: SIX, EIGHT, TEN, TWELVE, FOURTEEN, SIXTEEN.";
    }

    if (fullMessage != null && fullMessage.contains("ShoeSize")) {
      return "Invalid shoe size. Must be one of: 5, 6, 7, 8, 9, 10, 11, 12.";
    }

    if (fullMessage != null && fullMessage.contains("LaneStatus")) {
      return "Invalid lane status. Must be one of: AVAILABLE, IN_USE, MAINTENANCE.";
    }

    if (fullMessage != null && fullMessage.contains("TransactionStatus")) {
      return "Invalid transaction status. Must be one of: OPEN, COMPLETED, CANCELLED.";
    }

    return "Invalid request body.";
  }
}
