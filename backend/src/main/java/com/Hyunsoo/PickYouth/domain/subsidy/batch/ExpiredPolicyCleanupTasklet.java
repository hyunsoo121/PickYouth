package com.Hyunsoo.PickYouth.domain.subsidy.batch;

import com.Hyunsoo.PickYouth.domain.subsidy.repository.SubsidyRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

/** 마감(applyEnd)된 지 {@link #RETENTION_YEARS}년 지난 정책을 매 배치 실행마다 삭제한다. */
@Component
public class ExpiredPolicyCleanupTasklet implements Tasklet {

  private static final int RETENTION_YEARS = 1;

  private static final Logger log = LoggerFactory.getLogger(ExpiredPolicyCleanupTasklet.class);

  private final SubsidyRepository subsidyRepository;

  public ExpiredPolicyCleanupTasklet(SubsidyRepository subsidyRepository) {
    this.subsidyRepository = subsidyRepository;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    LocalDate cutoff = LocalDate.now().minusYears(RETENTION_YEARS);
    long deleted = subsidyRepository.deleteByApplyEndBefore(cutoff);
    log.info("마감 {}년 지난 정책 {}건 삭제 (cutoff={})", RETENTION_YEARS, deleted, cutoff);
    return RepeatStatus.FINISHED;
  }
}
