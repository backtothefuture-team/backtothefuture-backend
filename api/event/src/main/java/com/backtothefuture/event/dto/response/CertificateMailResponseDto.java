package com.backtothefuture.event.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CertificateMailResponseDto(
        @Schema(description = "인증 번호 유효 시간(초)", example = "600")
        int mailExpirationSeconds,

        @Schema(description = "인증 번호", example = "123456")
        String certificationNumber
) {
}
