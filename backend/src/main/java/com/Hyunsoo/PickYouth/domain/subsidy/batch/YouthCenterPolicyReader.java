package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import com.Hyunsoo.PickYouth.domain.subsidy.client.YouthCenterApiClient;
import com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthCenterResponse;
import com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthPolicyDto;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.infrastructure.item.ItemReader;

/**
 * 온통청년 API를 페이지 단위로 순회하며 정책을 한 건씩 반환한다. 페이지 커서를 필드로 들고 있는 상태 객체라 Job 실행마다 새 인스턴스여야 한다 — 반드시
 * {@code @StepScope} 빈으로만 생성한다 (CollectPolicyJobConfig 참고).
 */
public class YouthCenterPolicyReader implements ItemReader<YouthPolicyDto> {

  private final YouthCenterApiClient apiClient;
  private final int pageSize;

  private Iterator<YouthPolicyDto> currentPage = List.<YouthPolicyDto>of().iterator();
  private int nextPageNum = 1;
  private boolean exhausted = false;

  public YouthCenterPolicyReader(YouthCenterApiClient apiClient, int pageSize) {
    this.apiClient = apiClient;
    this.pageSize = pageSize;
  }

  @Override
  public YouthPolicyDto read() {
    if (exhausted) {
      return null;
    }
    if (!currentPage.hasNext()) {
      fetchNextPage();
    }
    if (!currentPage.hasNext()) {
      exhausted = true;
      return null;
    }
    return currentPage.next();
  }

  private void fetchNextPage() {
    YouthCenterResponse response = apiClient.fetchPolicies(nextPageNum, pageSize);
    currentPage = response.result().youthPolicyList().iterator();
    nextPageNum++;
  }
}
