package com.Hyunsoo.PickYouth.domain.subsidy.dto;

import java.time.LocalDate;

/** 정책의 신청 기간(applyStart~applyEnd)을 기준일과 비교해 판별하는 시행 상태. */
public enum SubsidyStatus {
  UPCOMING,
  ONGOING,
  ENDED;

  // applyStart/applyEnd가 둘 다 null인 상시/제한없음 정책은 항상 ONGOING으로 판별된다.
  public static SubsidyStatus of(LocalDate applyStart, LocalDate applyEnd, LocalDate today) {
    if (applyStart != null && today.isBefore(applyStart)) {
      return UPCOMING;
    }
    if (applyEnd != null && today.isAfter(applyEnd)) {
      return ENDED;
    }
    return ONGOING;
  }
}
