package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import com.Hyunsoo.PickYouth.domain.subsidy.client.YouthCenterApiClient;
import com.Hyunsoo.PickYouth.domain.subsidy.client.YouthCenterProperties;
import com.Hyunsoo.PickYouth.domain.subsidy.client.dto.YouthPolicyDto;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/** 온통청년 정책 수집 배치: 1개 Step(fetch-and-upsert)로 API 전량을 조회해 Subsidy 테이블에 반영한다. */
@Configuration
@EnableConfigurationProperties(YouthCenterProperties.class)
public class CollectPolicyJobConfig {

  /** 실측 결과 pageSize=2000이면 totCount(2,715건 기준) 2페이지로 전량 수집됨. */
  private static final int PAGE_SIZE = 2000;

  private static final int CHUNK_SIZE = 200;

  private final YouthCenterApiClient apiClient;
  private final PolicyDtoToEntityProcessor processor;
  private final SubsidyUpsertWriter writer;

  public CollectPolicyJobConfig(
      YouthCenterApiClient apiClient,
      PolicyDtoToEntityProcessor processor,
      SubsidyUpsertWriter writer) {
    this.apiClient = apiClient;
    this.processor = processor;
    this.writer = writer;
  }

  @Bean
  @StepScope
  public YouthCenterPolicyReader youthCenterPolicyReader() {
    return new YouthCenterPolicyReader(apiClient, PAGE_SIZE);
  }

  @Bean
  public Step collectPolicyStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      YouthCenterPolicyReader youthCenterPolicyReader) {
    return new StepBuilder("collectPolicyStep", jobRepository)
        .<YouthPolicyDto, SubsidyUpsertData>chunk(CHUNK_SIZE)
        .reader(youthCenterPolicyReader)
        .processor(processor)
        .writer(writer)
        .transactionManager(transactionManager)
        .build();
  }

  @Bean
  public Job collectPolicyJob(JobRepository jobRepository, Step collectPolicyStep) {
    return new JobBuilder("collectPolicyJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(collectPolicyStep)
        .build();
  }
}
