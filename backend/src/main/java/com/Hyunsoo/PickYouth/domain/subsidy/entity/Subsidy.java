package com.Hyunsoo.PickYouth.domain.subsidy.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 온통청년 API(getPlcy) 응답을 매핑하는 정책/지원금 엔티티. 필드-API 매핑은 PickYouth.md 5절 참고.
 *
 * <p>description/supportContent는 상세 모달(MVP 2절)에 필요해 plcyExplnCn/plcySprtCn을 매핑용으로 추가한 필드.
 */
@Entity
@Table(name = "subsidy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subsidy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String plcyNo;

  @Column(nullable = false)
  private String title;

  @Lob private String description;

  @Lob private String supportContent;

  private String org;

  private String categoryLarge;

  private String categoryMid;

  private Integer ageMin;

  private Integer ageMax;

  private String ageLimitYn;

  private String schoolCd;

  private String jobCd;

  private String majorCd;

  private String marriageCd;

  private String incomeCondCd;

  private Long incomeMin;

  private Long incomeMax;

  private String specialCd;

  private String applyPeriodRaw;

  private LocalDate applyStart;

  private LocalDate applyEnd;

  /** 실측 결과 varchar(255)를 넘는 URL이 존재해(쿼리스트링 포함) TEXT로 둔다. */
  @Column(columnDefinition = "text")
  private String applyUrl;

  @Column(columnDefinition = "text")
  private String refUrl1;

  @Column(columnDefinition = "text")
  private String refUrl2;

  private LocalDateTime firstRegDt;

  private LocalDateTime lastMdfcnDt;

  @OneToMany(mappedBy = "subsidy", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SubsidyRegion> regions = new ArrayList<>();

  @Builder
  public Subsidy(
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
      LocalDateTime lastMdfcnDt) {
    this.plcyNo = plcyNo;
    this.title = title;
    this.description = description;
    this.supportContent = supportContent;
    this.org = org;
    this.categoryLarge = categoryLarge;
    this.categoryMid = categoryMid;
    this.ageMin = ageMin;
    this.ageMax = ageMax;
    this.ageLimitYn = ageLimitYn;
    this.schoolCd = schoolCd;
    this.jobCd = jobCd;
    this.majorCd = majorCd;
    this.marriageCd = marriageCd;
    this.incomeCondCd = incomeCondCd;
    this.incomeMin = incomeMin;
    this.incomeMax = incomeMax;
    this.specialCd = specialCd;
    this.applyPeriodRaw = applyPeriodRaw;
    this.applyStart = applyStart;
    this.applyEnd = applyEnd;
    this.applyUrl = applyUrl;
    this.refUrl1 = refUrl1;
    this.refUrl2 = refUrl2;
    this.firstRegDt = firstRegDt;
    this.lastMdfcnDt = lastMdfcnDt;
  }

  public void addRegion(SubsidyRegion region) {
    regions.add(region);
    region.assignSubsidy(this);
  }

  /** 배치 재수집 시 기존 row를 최신 값으로 갱신한다. firstRegDt는 최초 등록 시점 그대로 유지한다. */
  public void update(
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
      LocalDateTime lastMdfcnDt) {
    this.title = title;
    this.description = description;
    this.supportContent = supportContent;
    this.org = org;
    this.categoryLarge = categoryLarge;
    this.categoryMid = categoryMid;
    this.ageMin = ageMin;
    this.ageMax = ageMax;
    this.ageLimitYn = ageLimitYn;
    this.schoolCd = schoolCd;
    this.jobCd = jobCd;
    this.majorCd = majorCd;
    this.marriageCd = marriageCd;
    this.incomeCondCd = incomeCondCd;
    this.incomeMin = incomeMin;
    this.incomeMax = incomeMax;
    this.specialCd = specialCd;
    this.applyPeriodRaw = applyPeriodRaw;
    this.applyStart = applyStart;
    this.applyEnd = applyEnd;
    this.applyUrl = applyUrl;
    this.refUrl1 = refUrl1;
    this.refUrl2 = refUrl2;
    this.lastMdfcnDt = lastMdfcnDt;
  }

  /** zipCd 목록을 최신 상태로 통째로 교체한다 (orphanRemoval로 기존 region row는 자동 삭제). */
  public void replaceRegions(List<String> zipCodes) {
    regions.clear();
    zipCodes.forEach(zipCd -> addRegion(new SubsidyRegion(zipCd)));
  }

  /** aplyUrlAddr가 빈 문자열일 때 refUrlAddr1 → refUrlAddr2 순으로 폴백해 신청 링크를 반환한다. */
  public String resolveApplyUrl() {
    if (applyUrl != null && !applyUrl.isBlank()) {
      return applyUrl;
    }
    if (refUrl1 != null && !refUrl1.isBlank()) {
      return refUrl1;
    }
    return refUrl2;
  }
}
