package com.backtothefuture.store.config;


import com.backtothefuture.domain.common.util.fcm.FCMUtil;
import com.backtothefuture.infra.config.BatchConfig;
import com.backtothefuture.store.dto.RegistrationTokenMappingDto;
import com.backtothefuture.store.util.batch.FCMWriter;
import com.backtothefuture.store.util.batch.RegistrationTokenItemProcessor;
import com.backtothefuture.store.util.batch.RegistrationTokenMapper;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMBatchConfig extends BatchConfig {

    /*
     현재 시간 기준으로 예약 시간이 30~31분 뒤인 예약을 접수한 고객들의 기기 등록 토큰을 조회
     */
    private final String QUERY =
            "select member.registration_token from member join reservation on member.member_id = reservation.member_id "
                    + "where reservation.reservation_time > DATE_ADD(NOW(), INTERVAL 30 MINUTE) and reservation.reservation_time < DATE_ADD(NOW(), INTERVAL 31 MINUTE)";
    private final RegistrationTokenMapper registrationTokenMapper;
    private final RegistrationTokenItemProcessor registrationTokenItemProcessor;
    private final FCMUtil fcmUtil;
    private final Integer CHUNK_SIZE = 100;
    private final DataSource dataSource;

    /**
     * FCM Job 등록
     */
    @Bean
    public Job remindMessageJob(JobRepository jobRepository, Step smapleStep) {
        return new JobBuilder("remindMessageJob", jobRepository)
                .start(smapleStep)
                .build();
    }

    /**
     * FCM Job의 Step 등록
     */
    @Bean
    public Step remindMessageStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager) {
        return new StepBuilder("remindMessageStep", jobRepository)
                .<RegistrationTokenMappingDto, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(getReservationReader())
                .processor(registrationTokenItemProcessor)
                .writer(getReservationWriter())
                .build();
    }

    /**
     * FCM ItemReader 등록
     */
    @Bean
    public JdbcCursorItemReader<RegistrationTokenMappingDto> getReservationReader() {
        return new JdbcCursorItemReaderBuilder<RegistrationTokenMappingDto>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .sql(QUERY)
                .rowMapper(registrationTokenMapper)
                .name("reservationItemReader")
                .build();
    }

    /**
     * FCM ItemWriter 등록
     */
    @Bean
    public ItemWriter<String> getReservationWriter() {
        return new FCMWriter<>(fcmUtil);
    }

}
