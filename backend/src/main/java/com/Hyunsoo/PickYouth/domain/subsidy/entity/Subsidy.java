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

  private String marriageCd;

  private String incomeCondCd;

  private Long incomeMin;

  private Long incomeMax;

  private String applyPeriodRaw;

  private LocalDate applyStart;

  private LocalDate applyEnd;

  private String applyUrl;

  private String refUrl1;

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
      String marriageCd,
      String incomeCondCd,
      Long incomeMin,
      Long incomeMax,
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
    this.marriageCd = marriageCd;
    this.incomeCondCd = incomeCondCd;
    this.incomeMin = incomeMin;
    this.incomeMax = incomeMax;
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
