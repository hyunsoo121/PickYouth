package com.Hyunsoo.PickYouth.domain.subsidy.repository;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsidyRepository extends JpaRepository<Subsidy, Long>, SubsidyQueryRepository {

  Optional<Subsidy> findByPlcyNo(String plcyNo);

  /** orphanRemoval을 통해 연관 SubsidyRegion도 함께 삭제된다 (엔티티 단위 remove()로 처리되어 FK 위반 없음). */
  long deleteByApplyEndBefore(LocalDate cutoff);
}
