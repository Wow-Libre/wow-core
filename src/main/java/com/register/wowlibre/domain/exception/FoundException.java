package com.register.wowlibre.domain.exception;

import org.springframework.http.HttpStatus;

public class FoundException extends GenericErrorException {
  public FoundException(String message, String transactionId) {
    super(transactionId, message, HttpStatus.CONFLICT);
  }
}
