package com.backtothefuture.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "LoginTokenDto", description = "access, refresh token 반환 model")
public record LoginTokenDto(
        @Schema(name = "memberId", description = "회원 아이디")
        Long memberId,
        @Schema(name = "accessToken", description = "엑세스 토큰 값")
        String accessToken,
        @Schema(name = "refreshToken", description = "리프래시 토큰 값")
        String refreshToken
) {
}
