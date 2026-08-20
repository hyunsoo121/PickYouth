package com.Hyunsoo.PickYouth.domain.subsidy.dto;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import java.time.LocalDate;
import java.util.List;

/** {@code GET /api/subsidies/{id}} 상세 조회 응답. */
public record SubsidyDetailResponse(
    Long id,
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
    LocalDate applyStart,
    LocalDate applyEnd,
    SubsidyStatus status,
    String applyUrl,
    List<String> zipCodes) {

  public static SubsidyDetailResponse from(Subsidy subsidy) {
    return new SubsidyDetailResponse(
        subsidy.getId(),
        subsidy.getPlcyNo(),
        subsidy.getTitle(),
        subsidy.getDescription(),
        subsidy.getSupportContent(),
        subsidy.getOrg(),
        subsidy.getCategoryLarge(),
        subsidy.getCategoryMid(),
        subsidy.getAgeMin(),
        subsidy.getAgeMax(),
        subsidy.getAgeLimitYn(),
        subsidy.getSchoolCd(),
        subsidy.getJobCd(),
        subsidy.getMajorCd(),
        subsidy.getMarriageCd(),
        subsidy.getIncomeCondCd(),
        subsidy.getIncomeMin(),
        subsidy.getIncomeMax(),
        subsidy.getSpecialCd(),
        subsidy.getApplyStart(),
        subsidy.getApplyEnd(),
        SubsidyStatus.of(subsidy.getApplyStart(), subsidy.getApplyEnd(), LocalDate.now()),
        subsidy.resolveApplyUrl(),
        subsidy.getRegions().stream().map(r -> r.getZipCd()).toList());
  }
}
