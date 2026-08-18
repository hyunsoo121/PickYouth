package com.Hyunsoo.PickYouth.domain.subsidy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Subsidy.zipCd는 콤마로 구분된 다중값이라 1:N으로 정규화한 테이블 (PickYouth.md 6절 참고). */
@Entity
@Table(name = "subsidy_region")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubsidyRegion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subsidy_id", nullable = false)
  private Subsidy subsidy;

  @Column(nullable = false)
  private String zipCd;

  public SubsidyRegion(String zipCd) {
    this.zipCd = zipCd;
  }

  void assignSubsidy(Subsidy subsidy) {
    this.subsidy = subsidy;
  }
}
