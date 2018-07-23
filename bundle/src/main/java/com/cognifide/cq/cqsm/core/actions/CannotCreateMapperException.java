package com.cognifide.cq.cqsm.core.actions;

public class CannotCreateMapperException extends RuntimeException {

  public CannotCreateMapperException(String message) {
    super(message);
  }

  public CannotCreateMapperException(String message, Throwable cause) {
    super(message, cause);
  }
}
