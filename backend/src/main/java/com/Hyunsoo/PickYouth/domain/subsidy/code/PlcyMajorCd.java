package com.Hyunsoo.PickYouth.domain.subsidy.code;

/**
 * 온통청년 API의 plcyMajorCd(전공요건) 공통코드. 코드 범위는 11001~11009(계열별 + 제한없음)로 확인됐으나, 코드별 정확한 한글 라벨은 아직 검증되지
 * 않아 {@link SchoolCd}/{@link JobCd}/{@link SbizCd}처럼 라벨 enum으로 만들지 않았다 — 틀린 라벨을 매칭 로직에 하드코딩하는 위험을
 * 피하기 위함. 매칭에는 정규화된 코드값만 쓰고, UI 라벨이 필요해지면 실제 코드정의서/API로 라벨을 검증한 뒤 enum으로 승격한다.
 */
public final class PlcyMajorCd {

  private PlcyMajorCd() {}

  public static String normalize(String rawApiCode) {
    if (rawApiCode != null && rawApiCode.length() == 7 && rawApiCode.startsWith("00")) {
      return rawApiCode.substring(2);
    }
    return rawApiCode;
  }
}
