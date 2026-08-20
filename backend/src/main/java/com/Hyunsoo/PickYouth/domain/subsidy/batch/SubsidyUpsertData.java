package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthPolicyDto}를 정규화/파싱해 upsert에 바로 쓸 수 있게
 * 만든 중간 표현.
 */
public record SubsidyUpsertData(
    String plcyNo,
    String title,
    String description,
    String supportContent,
    String org,
    String categoryLarge,
    String categoryMid,
    Integer ageMin,
    Integer ageMax,
    String ageLimitYn,
    String schoolCd,
    String jobCd,
    String majorCd,
    String marriageCd,
    String incomeCondCd,
    Long incomeMin,
    Long incomeMax,
    String specialCd,
    String applyPeriodRaw,
    LocalDate applyStart,
    LocalDate applyEnd,
    String applyUrl,
    String refUrl1,
    String refUrl2,
    LocalDateTime firstRegDt,
    LocalDateTime lastMdfcnDt,
    List<String> zipCodes) {}
