package com.Hyunsoo.PickYouth.domain.alert.entity;

import com.Hyunsoo.PickYouth.domain.alert.code.AlertChannel;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alert_subscription")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertSubscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private Integer age;

  private String region;

  private String schoolCd;

  private Long income;

  @Enumerated(EnumType.STRING)
  private AlertChannel channel;

  private boolean active;

  @Builder
  public AlertSubscription(
      User user, Integer age, String region, String schoolCd, Long income, AlertChannel channel) {
    this.user = user;
    this.age = age;
    this.region = region;
    this.schoolCd = schoolCd;
    this.income = income;
    this.channel = channel;
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }
}
