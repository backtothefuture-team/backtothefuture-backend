package com.backtothefuture.member.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegistrationTokenRequestDto(
        @Schema(description = "사용자의 기기 등록 토큰 값입이다.")
        @NotBlank(message = "기기 등록 토큰은 필수 값입니다.")
        String registrationToken
) {
}
