package com.Hyunsoo.PickYouth.domain.subsidy.repository;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsidyRepository extends JpaRepository<Subsidy, Long>, SubsidyQueryRepository {

  Optional<Subsidy> findByPlcyNo(String plcyNo);
}
