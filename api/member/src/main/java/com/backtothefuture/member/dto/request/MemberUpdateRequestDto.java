package com.backtothefuture.member.dto.request;

import com.backtothefuture.member.annotation.NumericStringList;
import com.backtothefuture.member.annotation.ValidyyyyMMdd;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MemberUpdateRequestDto(

        @Schema(description = "닉네임", example = "김길동")
        String name,                            // 닉네임

        @ArraySchema(arraySchema = @Schema(description = "핸드폰 전화번호", example = "[\"010\", \"1234\", \"5678\"]"))
        @NumericStringList
        List<String> phoneNumber,               // 전화번호

        @ValidyyyyMMdd
        @Schema(description = "생년월일, yyyyMMdd 포맷", example = "20000101")
        String birth,                           // 생년월일

        @Schema(description = "계좌 정보", implementation = AccountInfoDto.class)
        AccountInfoDto accountInfo,              // 계좌 정보

        @Schema(description = "거주지 정보", implementation = ResidenceInfoDto.class)
        ResidenceInfoDto residenceInfo           // 거주지 정보
) {
}
