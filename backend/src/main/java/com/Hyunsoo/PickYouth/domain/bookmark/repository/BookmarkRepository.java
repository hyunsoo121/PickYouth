package com.Hyunsoo.PickYouth.domain.bookmark.repository;

import com.Hyunsoo.PickYouth.domain.bookmark.entity.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  List<Bookmark> findByUserId(Long userId);

  Optional<Bookmark> findByUserIdAndSubsidyId(Long userId, Long subsidyId);

  void deleteByUserIdAndSubsidyId(Long userId, Long subsidyId);
}
