package com.Hyunsoo.PickYouth.domain.bookmark.entity;

import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.Hyunsoo.PickYouth.domain.user.entity.User;
import com.Hyunsoo.PickYouth.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "bookmark",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_bookmark_user_subsidy",
            columnNames = {"user_id", "subsidy_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseCreatedAtEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subsidy_id", nullable = false)
  private Subsidy subsidy;

  @Builder
  public Bookmark(User user, Subsidy subsidy) {
    this.user = user;
    this.subsidy = subsidy;
  }
}
