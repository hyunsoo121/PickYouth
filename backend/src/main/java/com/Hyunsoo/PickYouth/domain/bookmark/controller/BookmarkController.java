package com.Hyunsoo.PickYouth.domain.bookmark.controller;

import com.Hyunsoo.PickYouth.domain.bookmark.service.BookmarkService;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bookmark", description = "관심 정책 (회원 전용)")
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

  private final BookmarkService bookmarkService;

  public BookmarkController(BookmarkService bookmarkService) {
    this.bookmarkService = bookmarkService;
  }

  @Operation(summary = "관심 등록", description = "이미 등록된 정책이면 그대로 둔다(멱등).")
  @PostMapping("/{subsidyId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void add(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long subsidyId) {
    bookmarkService.add(userDetails.getUsername(), subsidyId);
  }

  @Operation(summary = "관심 해제", description = "등록되어 있지 않아도 에러 없이 그대로 둔다(멱등).")
  @DeleteMapping("/{subsidyId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long subsidyId) {
    bookmarkService.remove(userDetails.getUsername(), subsidyId);
  }

  @Operation(summary = "내 관심 목록 조회")
  @GetMapping
  public List<SubsidySummaryResponse> list(@AuthenticationPrincipal UserDetails userDetails) {
    return bookmarkService.list(userDetails.getUsername());
  }
}
