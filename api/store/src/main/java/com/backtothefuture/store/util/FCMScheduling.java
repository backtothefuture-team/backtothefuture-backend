package com.backtothefuture.store.util;

import com.backtothefuture.store.config.FCMBatchConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FCMScheduling {

    private final FCMBatchConfig fcmBatchConfig;
    private final Job remindMessageJob;
    private final JobLauncher jobLauncher;

    /**
     * 예약 시간 30분 전 알림 발송 batch 작업을 1분마다 스케줄링
     */
    @Scheduled(cron = "0 * * * * *")
    public void schedule()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters param = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // JobParameters 세팅 for JobInstance 값
                .toJobParameters();

        jobLauncher.run(remindMessageJob, param);
    }
}
