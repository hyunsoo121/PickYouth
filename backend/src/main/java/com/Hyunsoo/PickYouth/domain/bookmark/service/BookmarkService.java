package com.Hyunsoo.PickYouth.domain.bookmark.service;

import com.Hyunsoo.PickYouth.domain.bookmark.entity.Bookmark;
import com.Hyunsoo.PickYouth.domain.bookmark.repository.BookmarkRepository;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySummaryResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.SubsidyNotFoundException;
import com.Hyunsoo.PickYouth.domain.subsidy.repository.SubsidyRepository;
import com.Hyunsoo.PickYouth.domain.user.entity.User;
import com.Hyunsoo.PickYouth.domain.user.exception.UserNotFoundException;
import com.Hyunsoo.PickYouth.domain.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 관심(찜) 정책 — 회원 전용. add/remove는 이미 그 상태여도 에러 없이 그대로 두는 멱등 토글로 동작한다. */
@Service
@Transactional(readOnly = true)
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final SubsidyRepository subsidyRepository;
  private final UserRepository userRepository;

  public BookmarkService(
      BookmarkRepository bookmarkRepository,
      SubsidyRepository subsidyRepository,
      UserRepository userRepository) {
    this.bookmarkRepository = bookmarkRepository;
    this.subsidyRepository = subsidyRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void add(String email, Long subsidyId) {
    User user = getUser(email);
    if (bookmarkRepository.findByUserIdAndSubsidyId(user.getId(), subsidyId).isPresent()) {
      return;
    }
    Subsidy subsidy =
        subsidyRepository.findById(subsidyId).orElseThrow(() -> new SubsidyNotFoundException(subsidyId));
    bookmarkRepository.save(Bookmark.builder().user(user).subsidy(subsidy).build());
  }

  @Transactional
  public void remove(String email, Long subsidyId) {
    User user = getUser(email);
    bookmarkRepository.deleteByUserIdAndSubsidyId(user.getId(), subsidyId);
  }

  public List<SubsidySummaryResponse> list(String email) {
    User user = getUser(email);
    return bookmarkRepository.findByUserId(user.getId()).stream()
        .map(bookmark -> SubsidySummaryResponse.from(bookmark.getSubsidy()))
        .toList();
  }

  private User getUser(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
  }
}
