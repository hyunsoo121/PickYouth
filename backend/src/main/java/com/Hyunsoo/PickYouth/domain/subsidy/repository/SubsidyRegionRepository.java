package com.Hyunsoo.PickYouth.domain.subsidy.repository;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.SubsidyRegion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsidyRegionRepository extends JpaRepository<SubsidyRegion, Long> {

  List<SubsidyRegion> findBySubsidyId(Long subsidyId);

  List<SubsidyRegion> findByZipCd(String zipCd);
}
