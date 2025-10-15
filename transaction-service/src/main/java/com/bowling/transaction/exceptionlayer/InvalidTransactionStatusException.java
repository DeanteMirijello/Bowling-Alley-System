package com.bowling.transaction.exceptionlayer;

public class InvalidTransactionStatusException extends RuntimeException {

  public InvalidTransactionStatusException(String message) {
    super(message);
  }
}

