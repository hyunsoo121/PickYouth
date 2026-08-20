package com.Hyunsoo.PickYouth.domain.subsidy.client;

import com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthCenterResponse;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 온통청년 API(getPlcy) 호출 클라이언트. 실측 결과 pageSize=10000까지도 정상 응답하지만, 빠른 연속 호출 시 간헐적으로 429/400성 응답이 발생해
 * 재시도를 필수로 둔다.
 */
@Component
public class YouthCenterApiClient {

  private static final Logger log = LoggerFactory.getLogger(YouthCenterApiClient.class);
  private static final int MAX_ATTEMPTS = 3;
  private static final long BASE_BACKOFF_MS = 500;

  private final RestClient restClient;
  private final YouthCenterProperties properties;

  public YouthCenterApiClient(YouthCenterProperties properties) {
    this.properties = properties;
    this.restClient = RestClient.builder().baseUrl(properties.baseUrl()).build();
  }

  public YouthCenterResponse fetchPolicies(int pageNum, int pageSize) {
    return withRetry(
        () ->
            restClient
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .queryParam("apiKeyNm", properties.apiKey())
                            .queryParam("pageNum", pageNum)
                            .queryParam("pageSize", pageSize)
                            .queryParam("rtnType", "json")
                            .build())
                .retrieve()
                .body(YouthCenterResponse.class));
  }

  private <T> T withRetry(Supplier<T> call) {
    RuntimeException lastFailure = null;
    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
      try {
        return call.get();
      } catch (RestClientException e) {
        lastFailure = e;
        log.warn("온통청년 API 호출 실패 ({}번째 시도): {}", attempt, e.getMessage());
        if (attempt < MAX_ATTEMPTS) {
          sleep(BASE_BACKOFF_MS * attempt);
        }
      }
    }
    throw new IllegalStateException("온통청년 API 호출이 " + MAX_ATTEMPTS + "회 모두 실패했습니다.", lastFailure);
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("재시도 대기 중 인터럽트됨", e);
    }
  }
}
