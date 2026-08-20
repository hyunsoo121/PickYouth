package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.SubsidyRegion;
import com.Hyunsoo.PickYouth.domain.subsidy.repository.SubsidyRepository;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

/** {@code plcyNo} 기준 upsert. 신규면 insert, 기존이면 lastMdfcnDt가 갱신된 경우에만 update한다. */
@Component
public class SubsidyUpsertWriter implements ItemWriter<SubsidyUpsertData> {

  private final SubsidyRepository subsidyRepository;

  public SubsidyUpsertWriter(SubsidyRepository subsidyRepository) {
    this.subsidyRepository = subsidyRepository;
  }

  @Override
  public void write(Chunk<? extends SubsidyUpsertData> chunk) {
    for (SubsidyUpsertData data : chunk) {
      subsidyRepository
          .findByPlcyNo(data.plcyNo())
          .ifPresentOrElse(existing -> updateIfChanged(existing, data), () -> insert(data));
    }
  }

  private void insert(SubsidyUpsertData data) {
    Subsidy subsidy =
        Subsidy.builder()
            .plcyNo(data.plcyNo())
            .title(data.title())
            .description(data.description())
            .supportContent(data.supportContent())
            .org(data.org())
            .categoryLarge(data.categoryLarge())
            .categoryMid(data.categoryMid())
            .ageMin(data.ageMin())
            .ageMax(data.ageMax())
            .ageLimitYn(data.ageLimitYn())
            .schoolCd(data.schoolCd())
            .jobCd(data.jobCd())
            .majorCd(data.majorCd())
            .marriageCd(data.marriageCd())
            .incomeCondCd(data.incomeCondCd())
            .incomeMin(data.incomeMin())
            .incomeMax(data.incomeMax())
            .specialCd(data.specialCd())
            .applyPeriodRaw(data.applyPeriodRaw())
            .applyStart(data.applyStart())
            .applyEnd(data.applyEnd())
            .applyUrl(data.applyUrl())
            .refUrl1(data.refUrl1())
            .refUrl2(data.refUrl2())
            .firstRegDt(data.firstRegDt())
            .lastMdfcnDt(data.lastMdfcnDt())
            .build();
    data.zipCodes().forEach(zipCd -> subsidy.addRegion(new SubsidyRegion(zipCd)));
    subsidyRepository.save(subsidy);
  }

  private void updateIfChanged(Subsidy existing, SubsidyUpsertData data) {
    boolean unchanged =
        existing.getLastMdfcnDt() != null && existing.getLastMdfcnDt().equals(data.lastMdfcnDt());
    if (unchanged) {
      return;
    }
    existing.update(
        data.title(),
        data.description(),
        data.supportContent(),
        data.org(),
        data.categoryLarge(),
        data.categoryMid(),
        data.ageMin(),
        data.ageMax(),
        data.ageLimitYn(),
        data.schoolCd(),
        data.jobCd(),
        data.majorCd(),
        data.marriageCd(),
        data.incomeCondCd(),
        data.incomeMin(),
        data.incomeMax(),
        data.specialCd(),
        data.applyPeriodRaw(),
        data.applyStart(),
        data.applyEnd(),
        data.applyUrl(),
        data.refUrl1(),
        data.refUrl2(),
        data.lastMdfcnDt());
    existing.replaceRegions(data.zipCodes());
  }
}
