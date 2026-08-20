package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthPolicyDto;
import com.Hyunsoo.PickYouth.domain.subsidy.code.EarnCndSeCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.JobCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.MrgSttsCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.PlcyMajorCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SbizCd;
import com.Hyunsoo.PickYouth.domain.subsidy.code.SchoolCd;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * {@link YouthPolicyDto} → {@link SubsidyUpsertData} 변환. 코드값 정규화(7자리 00-prefix 제거), 날짜 파싱, zipCd
 * split을 담당한다 (PickYouth.md 5절 규칙 그대로 반영).
 */
@Component
public class PolicyDtoToEntityProcessor
    implements ItemProcessor<YouthPolicyDto, SubsidyUpsertData> {

  private static final Logger log = LoggerFactory.getLogger(PolicyDtoToEntityProcessor.class);
  private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter DATETIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public SubsidyUpsertData process(YouthPolicyDto dto) {
    String incomeCondCd = EarnCndSeCd.normalize(dto.earnCndSeCd());
    boolean isAnnualIncome = EarnCndSeCd.ANNUAL_INCOME.code().equals(incomeCondCd);

    LocalDate[] applyPeriod = parseApplyPeriod(dto.aplyYmd());

    return new SubsidyUpsertData(
        dto.plcyNo(),
        dto.plcyNm(),
        dto.plcyExplnCn(),
        dto.plcySprtCn(),
        dto.sprvsnInstCdNm(),
        dto.lclsfNm(),
        dto.mclsfNm(),
        parseIntOrNull(dto.sprtTrgtMinAge()),
        parseIntOrNull(dto.sprtTrgtMaxAge()),
        dto.sprtTrgtAgeLmtYn(),
        SchoolCd.normalize(dto.schoolCd()),
        JobCd.normalize(dto.jobCd()),
        PlcyMajorCd.normalize(dto.plcyMajorCd()),
        MrgSttsCd.normalize(dto.mrgSttsCd()),
        incomeCondCd,
        isAnnualIncome ? parseLongOrNull(dto.earnMinAmt()) : null,
        isAnnualIncome ? parseLongOrNull(dto.earnMaxAmt()) : null,
        SbizCd.normalize(dto.sbizCd()),
        dto.aplyYmd(),
        applyPeriod[0],
        applyPeriod[1],
        dto.aplyUrlAddr(),
        dto.refUrlAddr1(),
        dto.refUrlAddr2(),
        parseDateTimeOrNull(dto.frstRegDt()),
        parseDateTimeOrNull(dto.lastMdfcnDt()),
        splitZipCodes(dto.zipCd()));
  }

  /** {@code "20260807 ~ 20260930"} 형식. 상시모집 등으로 빈 문자열인 케이스는 [null, null]로 처리 (알림 대상에서 자연히 제외). */
  private LocalDate[] parseApplyPeriod(String raw) {
    if (raw == null || raw.isBlank()) {
      return new LocalDate[] {null, null};
    }
    String[] parts = raw.split("~");
    if (parts.length != 2) {
      log.warn("aplyYmd 파싱 실패, 형식 불일치: {}", raw);
      return new LocalDate[] {null, null};
    }
    LocalDate start = parseYmdOrNull(parts[0].trim());
    LocalDate end = parseYmdOrNull(parts[1].trim());
    return new LocalDate[] {start, end};
  }

  private LocalDate parseYmdOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return LocalDate.parse(value, YMD);
    } catch (Exception e) {
      log.warn("날짜 파싱 실패: {}", value);
      return null;
    }
  }

  private LocalDateTime parseDateTimeOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return LocalDateTime.parse(value, DATETIME);
    } catch (Exception e) {
      log.warn("일시 파싱 실패: {}", value);
      return null;
    }
  }

  private Integer parseIntOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Long parseLongOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Long.parseLong(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** zipCd는 콤마로 구분된 다중값 (예: {@code "11110,11140"}). 빈 문자열/공백 요소는 제거한다. */
  private List<String> splitZipCodes(String raw) {
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();
  }
}
