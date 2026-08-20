package com.Hyunsoo.PickYouth.domain.subsidy.repository;

import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySearchCondition;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubsidyQueryRepository {

  Page<Subsidy> search(SubsidySearchCondition condition, Pageable pageable);
}
