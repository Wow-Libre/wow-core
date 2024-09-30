package com.register.wowlibre.domain.exception;

import org.springframework.http.HttpStatus;

public class GenericErrorException extends RuntimeException {
  public final String transactionId;
  public final HttpStatus httpStatus;

  public GenericErrorException(String transactionId, String message, HttpStatus httpStatus) {
    super(message);
    this.transactionId = transactionId;
    this.httpStatus = httpStatus;
  }
}
