package com.Hyunsoo.PickYouth.domain.subsidy.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yaml의 {@code youthcenter.*} 설정 바인딩. record라 생성자 바인딩이 필요해 {@code @Component}로
 * 등록하면 안 되고(일반 DI 생성자로 오인됨), {@link com.Hyunsoo.PickYouth.domain.subsidy.batch.CollectPolicyJobConfig}의
 * {@code @EnableConfigurationProperties}로 등록한다.
 */
@ConfigurationProperties(prefix = "youthcenter")
public record YouthCenterProperties(String baseUrl, String apiKey) {}
