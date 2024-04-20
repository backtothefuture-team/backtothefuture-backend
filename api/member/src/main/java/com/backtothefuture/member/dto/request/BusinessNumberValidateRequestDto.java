package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record BusinessNumberValidateRequestDto(
        @Schema(description = "사업자 번호", required = true, example = "123-45-67890")
        String businessNumber
) {
}
