package com.Hyunsoo.PickYouth.domain.subsidy.repository;

import com.Hyunsoo.PickYouth.domain.subsidy.code.EarnCndSeCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.JobCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.MrgSttsCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SbizCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SchoolCd;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySearchCondition;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidyStatus;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.QSubsidy;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.QSubsidyRegion;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * {@code age}/{@code income}은 정책의 {@code ageLimitYn}/{@code incomeCondCd}가 "제한없음"이면 값과 무관하게 매칭하고,
 * 코드값 축({@code schoolCd}/{@code jobCd}/{@code marriageCd}/{@code specialCd})도 정책 쪽 코드가 "제한없음" 코드이면
 * 사용자 값과 무관하게 매칭한다 (각 코드북의 {@code NONE} 상수 참고).
 *
 * <p>{@code ageLimitYn}은 실측 데이터 기준 "N"이어도 age_min/age_max에 참고용 범위가 채워진 케이스가 많아, 필드명 그대로 "Y=연령 제한
 * 적용됨/N=연령 무관"으로 해석해 N이면 범위를 무시한다 — API 문서에 명시적 정의가 없어 이 필드명 기반 해석을 그대로 채택했다.
 */
@Repository
public class SubsidyQueryRepositoryImpl implements SubsidyQueryRepository {

  private final JPAQueryFactory queryFactory;

  public SubsidyQueryRepositoryImpl(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public Page<Subsidy> search(SubsidySearchCondition condition, Pageable pageable) {
    QSubsidy subsidy = QSubsidy.subsidy;

    BooleanBuilder where =
        new BooleanBuilder()
            .and(ageCondition(subsidy, condition.age()))
            .and(incomeCondition(subsidy, condition.income()))
            .and(codeOrNoneCondition(subsidy.schoolCd, SchoolCd.NONE.code(), condition.schoolCd()))
            .and(codeOrNoneCondition(subsidy.jobCd, JobCd.NONE.code(), condition.jobCd()))
            .and(
                codeOrNoneCondition(
                    subsidy.marriageCd, MrgSttsCd.NONE.code(), condition.marriageCd()))
            .and(codeOrNoneCondition(subsidy.specialCd, SbizCd.NONE.code(), condition.specialCd()))
            .and(equalsIfPresent(subsidy.majorCd, condition.majorCd()))
            .and(equalsIfPresent(subsidy.categoryLarge, condition.categoryLarge()))
            .and(equalsIfPresent(subsidy.categoryMid, condition.categoryMid()))
            .and(zipCondition(subsidy, condition.zipCd()))
            .and(statusCondition(subsidy, condition.status()));

    List<Subsidy> content =
        queryFactory
            .selectFrom(subsidy)
            .where(where)
            .orderBy(subsidy.applyStart.desc().nullsLast(), subsidy.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long total = queryFactory.select(subsidy.count()).from(subsidy).where(where).fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0 : total);
  }

  private BooleanExpression ageCondition(QSubsidy subsidy, Integer age) {
    if (age == null) {
      return null;
    }
    return subsidy.ageLimitYn.eq("N").or(subsidy.ageMin.loe(age).and(subsidy.ageMax.goe(age)));
  }

  private BooleanExpression incomeCondition(QSubsidy subsidy, Long income) {
    if (income == null) {
      return null;
    }
    return subsidy
        .incomeCondCd
        .ne(EarnCndSeCd.ANNUAL_INCOME.code())
        .or(subsidy.incomeMin.loe(income).and(subsidy.incomeMax.goe(income)));
  }

  private BooleanExpression codeOrNoneCondition(StringPath path, String noneCode, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return path.eq(noneCode).or(path.eq(value));
  }

  private BooleanExpression equalsIfPresent(StringPath path, String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return path.eq(value);
  }

  // SubsidyStatus.of()와 동일한 기준(오늘 날짜 vs applyStart/applyEnd)으로 판별한다.
  private BooleanExpression statusCondition(QSubsidy subsidy, SubsidyStatus status) {
    if (status == null) {
      return null;
    }
    LocalDate today = LocalDate.now();
    return switch (status) {
      case UPCOMING -> subsidy.applyStart.isNotNull().and(subsidy.applyStart.gt(today));
      case ENDED -> subsidy.applyEnd.isNotNull().and(subsidy.applyEnd.lt(today));
      case ONGOING -> {
        BooleanExpression startOk = subsidy.applyStart.isNull().or(subsidy.applyStart.loe(today));
        BooleanExpression endOk = subsidy.applyEnd.isNull().or(subsidy.applyEnd.goe(today));
        yield startOk.and(endOk);
      }
    };
  }

  // startsWith로 매칭해 시도 단위(2자리) 프리픽스든 정확한 5자리 zipCd든 둘 다 그대로 지원한다.
  private BooleanExpression zipCondition(QSubsidy subsidy, String zipCd) {
    if (zipCd == null || zipCd.isBlank()) {
      return null;
    }
    QSubsidyRegion region = QSubsidyRegion.subsidyRegion;
    return JPAExpressions.selectOne()
        .from(region)
        .where(region.subsidy.eq(subsidy), region.zipCd.startsWith(zipCd))
        .exists();
  }
}
