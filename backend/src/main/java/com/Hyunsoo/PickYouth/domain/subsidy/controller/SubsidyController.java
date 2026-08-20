package com.Hyunsoo.PickYouth.domain.subsidy.controller;

import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidyDetailResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidyPageResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.dto.SubsidySearchCondition;
import com.Hyunsoo.PickYouth.domain.subsidy.exception.InvalidPageSizeException;
import com.Hyunsoo.PickYouth.domain.subsidy.service.SubsidyQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subsidy", description = "청년 정책/지원금 조회 API (비회원 가능)")
@RestController
@RequestMapping("/api/subsidies")
public class SubsidyController {

  private static final int MAX_PAGE_SIZE = 100;

  private final SubsidyQueryService subsidyQueryService;

  public SubsidyController(SubsidyQueryService subsidyQueryService) {
    this.subsidyQueryService = subsidyQueryService;
  }

  @Operation(summary = "조건 매칭 정책 목록 조회", description = "모든 조건은 선택값이며, 비워두면 해당 축으로 필터링하지 않는다.")
  @GetMapping
  public SubsidyPageResponse search(
      @Valid @Parameter(description = "검색/매칭 조건") SubsidySearchCondition condition,
      @Parameter(hidden = true) Pageable pageable) {
    if (pageable.getPageSize() > MAX_PAGE_SIZE) {
      throw new InvalidPageSizeException(pageable.getPageSize(), MAX_PAGE_SIZE);
    }
    return subsidyQueryService.search(condition, pageable);
  }

  @Operation(summary = "정책 상세 조회")
  @GetMapping("/{id}")
  public SubsidyDetailResponse getDetail(@PathVariable Long id) {
    return subsidyQueryService.getDetail(id);
  }
}
