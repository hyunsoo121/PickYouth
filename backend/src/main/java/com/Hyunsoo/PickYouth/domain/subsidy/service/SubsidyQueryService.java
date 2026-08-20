package com.Hyunsoo.PickYouth.domain.subsidy.service;

import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidyDetailResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidyPageResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySearchCondition;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySummaryResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.SubsidyNotFoundException;
import com.Hyunsoo.PickYouth.domain.subsidy.repository.SubsidyRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SubsidyQueryService {

  private final SubsidyRepository subsidyRepository;

  public SubsidyQueryService(SubsidyRepository subsidyRepository) {
    this.subsidyRepository = subsidyRepository;
  }

  @Cacheable(value = "subsidySearch", key = "#condition.toString() + '|' + #pageable.toString()")
  public SubsidyPageResponse search(SubsidySearchCondition condition, Pageable pageable) {
    return SubsidyPageResponse.from(
        subsidyRepository.search(condition, pageable).map(SubsidySummaryResponse::from));
  }

  public SubsidyDetailResponse getDetail(Long id) {
    Subsidy subsidy =
        subsidyRepository.findById(id).orElseThrow(() -> new SubsidyNotFoundException(id));
    return SubsidyDetailResponse.from(subsidy);
  }
}
