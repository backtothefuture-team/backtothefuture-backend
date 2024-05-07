package com.backtothefuture.store.util.batch;

import com.backtothefuture.store.dto.RegistrationTokenMappingDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 *     @Bean
 *     public JdbcCursorItemReader<RegistrationTokenMappingDto> getReservationReader() {
 *         return new JdbcCursorItemReaderBuilder<RegistrationTokenMappingDto>()
 *                 .fetchSize(CHUNK_SIZE)
 *                 .dataSource(dataSource)
 *                 .sql(QUERY)
 *                 .rowMapper(registrationTokenMapper)
 *                 .name("reservationItemReader")
 *                 .build();
 *     }
 *
 *     위 ItemReader가 읽은 값(RegistrationTokenMappingDto)를 기기 등록 토큰 값(String)으로 변환하는 클래스입니다.
 */
@Component
public class RegistrationTokenItemProcessor implements ItemProcessor<RegistrationTokenMappingDto,String> {

    @Override
    public String process(RegistrationTokenMappingDto item) throws Exception {
        return item.getRegistrationToken();
    }
}
