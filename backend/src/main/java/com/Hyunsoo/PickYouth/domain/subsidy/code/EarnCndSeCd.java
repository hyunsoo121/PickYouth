package com.Hyunsoo.PickYouth.domain.subsidy.code;

import java.util.Arrays;

/** 온통청년 API의 earnCndSeCd(소득조건) 공통코드. 43002(연소득)일 때만 earnMinAmt/earnMaxAmt가 유효하다. */
public enum EarnCndSeCd {
  NONE("43001", "무관"),
  ANNUAL_INCOME("43002", "연소득"),
  OTHER("43003", "기타");

  private final String code;
  private final String label;

  EarnCndSeCd(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String code() {
    return code;
  }

  public String label() {
    return label;
  }

  public static String normalize(String rawApiCode) {
    if (rawApiCode != null && rawApiCode.length() == 7 && rawApiCode.startsWith("00")) {
      return rawApiCode.substring(2);
    }
    return rawApiCode;
  }

  public static EarnCndSeCd fromApiCode(String rawApiCode) {
    String normalized = normalize(rawApiCode);
    return Arrays.stream(values())
        .filter(c -> c.code.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 earnCndSeCd: " + rawApiCode));
  }
}
