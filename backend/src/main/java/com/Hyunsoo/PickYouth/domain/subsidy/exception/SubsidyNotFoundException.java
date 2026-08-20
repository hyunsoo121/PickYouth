package com.Hyunsoo.PickYouth.domain.subsidy.exception;

public class SubsidyNotFoundException extends RuntimeException {

  public SubsidyNotFoundException(Long id) {
    super("정책을 찾을 수 없습니다: id=" + id);
  }
}
