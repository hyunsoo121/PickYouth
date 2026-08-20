package com.Hyunsoo.PickYouth.domain.subsidy.exception;

public class InvalidPageSizeException extends RuntimeException {

  public InvalidPageSizeException(int size, int maxSize) {
    super("size는 " + maxSize + " 이하여야 합니다: size=" + size);
  }
}
