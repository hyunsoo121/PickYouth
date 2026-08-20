package com.Hyunsoo.PickYouth.domain.auth.exception;

public class TooManyAttemptsException extends RuntimeException {

  public TooManyAttemptsException() {
    super("너무 많은 시도가 있었습니다. 잠시 후 다시 시도해주세요.");
  }
}
