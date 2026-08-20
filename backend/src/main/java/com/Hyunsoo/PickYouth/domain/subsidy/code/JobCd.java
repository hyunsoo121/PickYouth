package com.Hyunsoo.PickYouth.domain.subsidy.code;

import java.util.Arrays;

/** 온통청년 API의 jobCd(취업요건) 공통코드. */
public enum JobCd {
  EMPLOYED("13001", "재직자"),
  SELF_EMPLOYED("13002", "자영업자"),
  UNEMPLOYED("13003", "미취업자"),
  FREELANCER("13004", "프리랜서"),
  DAY_LABORER("13005", "일용근로자"),
  PROSPECTIVE_STARTUP("13006", "예비창업자"),
  SHORT_TERM_WORKER("13007", "단기근로자"),
  FARMER("13008", "영농종사자"),
  OTHER("13009", "기타"),
  NONE("13010", "제한없음");

  private final String code;
  private final String label;

  JobCd(String code, String label) {
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

  public static JobCd fromApiCode(String rawApiCode) {
    String normalized = normalize(rawApiCode);
    return Arrays.stream(values())
        .filter(c -> c.code.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("알 수 없는 jobCd: " + rawApiCode));
  }
}
