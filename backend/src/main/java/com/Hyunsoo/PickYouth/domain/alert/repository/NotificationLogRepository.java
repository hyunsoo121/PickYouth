package com.Hyunsoo.PickYouth.domain.alert.repository;

import com.Hyunsoo.PickYouth.domain.alert.code.NotificationType;
import com.Hyunsoo.PickYouth.domain.alert.entity.NotificationLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

  boolean existsByUserIdAndSubsidyIdAndType(Long userId, Long subsidyId, NotificationType type);

  List<NotificationLog> findByUserId(Long userId);
}
