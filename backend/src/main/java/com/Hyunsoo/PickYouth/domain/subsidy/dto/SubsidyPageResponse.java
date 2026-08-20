package com.Hyunsoo.PickYouth.domain.subsidy.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * {@code GET /api/subsidies} 목록 응답. Spring Data {@code Page}를 그대로 직렬화/캐싱하지 않는다 — {@code PageImpl}은
 * Jackson 기본 역직렬화가 지원되지 않아(생성자 문제) Redis 캐시 왕복 시 깨질 수 있어, 이 record로 한 번 감싼다.
 */
public record SubsidyPageResponse(
    List<SubsidySummaryResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext) {

  public static SubsidyPageResponse from(Page<SubsidySummaryResponse> page) {
    return new SubsidyPageResponse(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.hasNext());
  }
}
