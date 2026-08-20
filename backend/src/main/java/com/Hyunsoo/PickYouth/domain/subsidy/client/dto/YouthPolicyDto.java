package com.Hyunsoo.PickYouth.domain.subsidy.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 온통청년 API(getPlcy) 응답의 정책 1건. 실제 응답은 이 외에도 수십 개 필드를 더 내려주지만, {@link
 * com.Hyunsoo.PickYouth.domain.subsidy.entity.Subsidy} 매핑에 필요한 필드만 선별했다 (PickYouth.md 5절 매핑표 참고).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record YouthPolicyDto(
    String plcyNo,
    String plcyNm,
    String plcyExplnCn,
    String plcySprtCn,
    String sprvsnInstCdNm,
    String lclsfNm,
    String mclsfNm,
    String sprtTrgtMinAge,
    String sprtTrgtMaxAge,
    String sprtTrgtAgeLmtYn,
    String schoolCd,
    String jobCd,
    String plcyMajorCd,
    String mrgSttsCd,
    String earnCndSeCd,
    String earnMinAmt,
    String earnMaxAmt,
    String sbizCd,
    String aplyYmd,
    String aplyUrlAddr,
    String refUrlAddr1,
    String refUrlAddr2,
    String frstRegDt,
    String lastMdfcnDt,
    String zipCd) {}
