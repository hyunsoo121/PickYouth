package com.Hyunsoo.PickYouth.domain.subsidy.dto;

import com.Hyunsoo.PickYouth.domain.subsidy.code.JobCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.MrgSttsCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SbizCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SchoolCd;
import com.Hyunsoo.PickYouth.domain.subsidy.validation.ValidCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * {@code GET /api/subsidies} 검색 조건. 모든 필드는 선택값이며, null/blank는 "해당 축으로 필터링하지 않음"을 의미한다.
 *
 * <p>{@code age}/{@code income}은 사용자 값이고, 정책의 {@code ageLimitYn}/{@code incomeCondCd}가 "제한없음"이면 해당
 * 정책은 사용자 값과 무관하게 매칭된다. 나머지 코드값 축({@code schoolCd}/{@code jobCd}/{@code marriageCd}/{@code
 * specialCd})도 정책 쪽 코드가 "제한없음"이면 사용자 값과 무관하게 매칭된다.
 */
public record SubsidySearchCondition(
    @Min(value = 0, message = "age는 0 이상이어야 합니다.") @Max(value = 120, message = "age는 120 이하여야 합니다.")
        Integer age,
    String zipCd,
    @ValidCode(value = SchoolCd.class, message = "유효하지 않은 schoolCd입니다.") String schoolCd,
    @ValidCode(value = JobCd.class, message = "유효하지 않은 jobCd입니다.") String jobCd,
    // PlcyMajorCd는 라벨이 검증되지 않아 enum이 아니므로 ValidCode 대신, 문서로 확인된 코드 범위(11001~11009)만 형식 검증한다.
    @Pattern(regexp = "^$|^1100[1-9]$", message = "유효하지 않은 majorCd입니다.") String majorCd,
    @ValidCode(value = MrgSttsCd.class, message = "유효하지 않은 marriageCd입니다.") String marriageCd,
    @PositiveOrZero(message = "income은 0 이상이어야 합니다.") Long income,
    String categoryLarge,
    String categoryMid,
    @ValidCode(value = SbizCd.class, message = "유효하지 않은 specialCd입니다.") String specialCd,
    SubsidyStatus status) {}
