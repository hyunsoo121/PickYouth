package com.Hyunsoo.PickYouth.domain.subsidy.code;

import java.util.Arrays;

/**
 * 온통청년 API의 schoolCd(학력요건) 공통코드. API 응답값은 5자리 코드 앞에 "00"이 붙은 7자리로 내려오므로 normalize()로 정규화 후 매칭한다.
 */
public enum SchoolCd {
  UNDER_HIGH_SCHOOL("49001", "고졸미만"),
  HIGH_SCHOOL_ENROLLED("49002", "고교재학"),
  HIGH_SCHOOL_EXPECTED_GRAD("49003", "고졸예정"),
  HIGH_SCHOOL_GRADUATED("49004", "고교졸업"),
  UNIVERSITY_ENROLLED("49005", "대학재학"),
  UNIVERSITY_EXPECTED_GRAD("49006", "대졸예정"),
  UNIVERSITY_GRADUATED("49007", "대학졸업"),
  GRADUATE_SCHOOL("49008", "석박사"),
  OTHER("49009", "기타"),
  NONE("49010", "제한없음");

  private final String code;
  private final String label;

  SchoolCd(String code, String label) {
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

  public static SchoolCd fromApiCode(String rawApiCode) {
    String normalized = normalize(rawApiCode);
    return Arrays.stream(values())
        .filter(c -> c.code.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 schoolCd: " + rawApiCode));
  }
}
