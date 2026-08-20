package com.Hyunsoo.PickYouth.domain.subsidy.code;

import java.util.Arrays;

/**
 * 온통청년 API의 sbizCd(특화요건) 공통코드. 코드정의서(엑셀)엔 {@code sBizCd}로 표기되어 있으나 실제 API 응답 필드명은 소문자 {@code
 * sbizCd}다.
 */
public enum SbizCd implements CodeEnum {
  SMALL_BUSINESS("14001", "중소기업"),
  WOMEN("14002", "여성"),
  BASIC_LIVELIHOOD("14003", "기초생활수급자"),
  SINGLE_PARENT("14004", "한부모"),
  DISABLED("14005", "장애인"),
  FARMER("14006", "농업인"),
  MILITARY("14007", "군인"),
  REGIONAL_TALENT("14008", "지역인재"),
  OTHER("14009", "기타"),
  NONE("14010", "제한없음");

  private final String code;
  private final String label;

  SbizCd(String code, String label) {
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

  public static SbizCd fromApiCode(String rawApiCode) {
    String normalized = normalize(rawApiCode);
    return Arrays.stream(values())
        .filter(c -> c.code.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 sbizCd: " + rawApiCode));
  }
}
