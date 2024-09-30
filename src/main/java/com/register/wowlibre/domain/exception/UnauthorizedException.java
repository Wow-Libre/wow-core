package com.register.wowlibre.domain.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GenericErrorException {
  public UnauthorizedException(String message, String transactionId) {
    super(transactionId, message, HttpStatus.UNAUTHORIZED);
  }
}
