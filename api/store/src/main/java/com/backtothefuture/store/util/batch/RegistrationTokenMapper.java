package com.backtothefuture.store.util.batch;

import com.backtothefuture.store.dto.RegistrationTokenMappingDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * DB Table의 Row와 Java의 객체(Object)를 Mapping 시켜주는 클래스입니다.
 */
@Component
public class RegistrationTokenMapper implements RowMapper<RegistrationTokenMappingDto> {

    public static final String REGISTRATION_COLUMN = "registration_token"; // table column 값

    @Override
    public RegistrationTokenMappingDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        RegistrationTokenMappingDto registrationTokenMappingDto = new RegistrationTokenMappingDto();
        registrationTokenMappingDto.setRegistrationToken(rs.getString(REGISTRATION_COLUMN));

        return registrationTokenMappingDto;
    }
}
