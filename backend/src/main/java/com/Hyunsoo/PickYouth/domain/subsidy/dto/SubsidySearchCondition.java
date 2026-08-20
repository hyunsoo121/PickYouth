package com.Hyunsoo.PickYouth.domain.subsidy.dto;

/**
 * {@code GET /api/subsidies} 검색 조건. 모든 필드는 선택값이며, null/blank는 "해당 축으로 필터링하지 않음"을 의미한다.
 *
 * <p>{@code age}/{@code income}은 사용자 값이고, 정책의 {@code ageLimitYn}/{@code incomeCondCd}가 "제한없음"이면 해당
 * 정책은 사용자 값과 무관하게 매칭된다. 나머지 코드값 축({@code schoolCd}/{@code jobCd}/{@code marriageCd}/{@code
 * specialCd})도 정책 쪽 코드가 "제한없음"이면 사용자 값과 무관하게 매칭된다.
 */
public record SubsidySearchCondition(
    Integer age,
    String zipCd,
    String schoolCd,
    String jobCd,
    String majorCd,
    String marriageCd,
    Long income,
    String categoryLarge,
    String categoryMid,
    String specialCd) {}
