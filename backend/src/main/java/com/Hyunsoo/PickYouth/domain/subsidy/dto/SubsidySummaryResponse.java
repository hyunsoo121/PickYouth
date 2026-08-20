package com.Hyunsoo.PickYouth.domain.subsidy.dto;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import java.time.LocalDate;

/** {@code GET /api/subsidies} 목록 조회용 요약 응답. */
public record SubsidySummaryResponse(
    Long id,
    String plcyNo,
    String title,
    String org,
    String categoryLarge,
    String categoryMid,
    LocalDate applyStart,
    LocalDate applyEnd) {

  public static SubsidySummaryResponse from(Subsidy subsidy) {
    return new SubsidySummaryResponse(
        subsidy.getId(),
        subsidy.getPlcyNo(),
        subsidy.getTitle(),
        subsidy.getOrg(),
        subsidy.getCategoryLarge(),
        subsidy.getCategoryMid(),
        subsidy.getApplyStart(),
        subsidy.getApplyEnd());
  }
}
