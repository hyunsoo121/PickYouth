package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 온통청년 정책 수집 배치를 주기적으로 트리거한다 (spring.batch.job.enabled=false로 앱 기동 시 자동실행은 막혀있음). */
@Component
public class PolicyCollectScheduler {

  private static final Logger log = LoggerFactory.getLogger(PolicyCollectScheduler.class);

  private final JobOperator jobOperator;
  private final Job collectPolicyJob;

  public PolicyCollectScheduler(JobOperator jobOperator, Job collectPolicyJob) {
    this.jobOperator = jobOperator;
    this.collectPolicyJob = collectPolicyJob;
  }

  /** 매일 새벽 4시 (정책 변경/API 트래픽이 뜸한 시간대). */
  @Scheduled(cron = "0 0 4 * * *")
  public void collect() {
    try {
      var jobParameters =
          new JobParametersBuilder()
              .addLong("timestamp", System.currentTimeMillis())
              .toJobParameters();
      jobOperator.start(collectPolicyJob, jobParameters);
    } catch (Exception e) {
      log.error("온통청년 정책 수집 배치 실행 실패", e);
    }
  }
}
