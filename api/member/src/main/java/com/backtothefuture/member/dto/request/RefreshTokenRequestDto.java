package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RefreshTokenRequestDto", description = "토큰 갱신 요청 model입니다.")
public record RefreshTokenRequestDto(
        @Schema(name = "refreshToken", description = "리프래시 토큰 값입니다.")
        @NotBlank(message = "refresh token 값은 필수입니다.")
        String refreshToken
) {
}
