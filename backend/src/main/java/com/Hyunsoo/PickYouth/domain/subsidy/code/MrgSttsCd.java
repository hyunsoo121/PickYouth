package com.Hyunsoo.PickYouth.domain.subsidy.code;

import java.util.Arrays;

/** 온통청년 API의 mrgSttsCd(결혼상태) 공통코드. */
public enum MrgSttsCd {
  MARRIED("55001", "기혼"),
  SINGLE("55002", "미혼"),
  NONE("55003", "제한없음");

  private final String code;
  private final String label;

  MrgSttsCd(String code, String label) {
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

  public static MrgSttsCd fromApiCode(String rawApiCode) {
    String normalized = normalize(rawApiCode);
    return Arrays.stream(values())
        .filter(c -> c.code.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 mrgSttsCd: " + rawApiCode));
  }
}
