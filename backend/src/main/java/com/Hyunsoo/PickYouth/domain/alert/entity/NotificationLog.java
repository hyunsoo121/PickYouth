package com.Hyunsoo.PickYouth.domain.alert.entity;

import com.Hyunsoo.PickYouth.domain.alert.code.AlertChannel;
import com.Hyunsoo.PickYouth.domain.alert.code.NotificationStatus;
import com.Hyunsoo.PickYouth.domain.alert.code.NotificationType;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.Hyunsoo.PickYouth.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 알림 발송 이력 — 배치 재실행 시 중복 발송/누락 방지 조회용 (PickYouth.md 4절). */
@Entity
@Table(name = "notification_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subsidy_id", nullable = false)
  private Subsidy subsidy;

  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  private AlertChannel channel;

  private LocalDateTime sentAt;

  @Enumerated(EnumType.STRING)
  private NotificationStatus status;

  @Builder
  public NotificationLog(
      User user,
      Subsidy subsidy,
      NotificationType type,
      AlertChannel channel,
      LocalDateTime sentAt,
      NotificationStatus status) {
    this.user = user;
    this.subsidy = subsidy;
    this.type = type;
    this.channel = channel;
    this.sentAt = sentAt;
    this.status = status;
  }
}
