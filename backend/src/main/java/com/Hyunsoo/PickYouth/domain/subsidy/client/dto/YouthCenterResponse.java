package com.Hyunsoo.PickYouth.domain.subsidy.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 온통청년 API(getPlcy) 응답 envelope. 실측 기준 {@code result.pagging} / {@code result.youthPolicyList} 구조.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record YouthCenterResponse(int resultCode, String resultMessage, Result result) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Result(Pagging pagging, List<YouthPolicyDto> youthPolicyList) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Pagging(int totCount, int pageNum, int pageSize) {}
}
