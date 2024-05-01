package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccountInfoDto(
        @Schema(description = "은행 코드", example = "06")
        String code,

        @Schema(description = "계좌 번호", example = "1234567890123456")
        String accountNumber
) {
}
