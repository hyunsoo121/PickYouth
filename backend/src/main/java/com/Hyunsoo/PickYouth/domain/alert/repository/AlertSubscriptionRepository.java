package com.Hyunsoo.PickYouth.domain.alert.repository;

import com.Hyunsoo.PickYouth.domain.alert.entity.AlertSubscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {

  List<AlertSubscription> findByUserId(Long userId);

  List<AlertSubscription> findByActiveTrue();
}
