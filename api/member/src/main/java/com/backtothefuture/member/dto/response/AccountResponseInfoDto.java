package com.backtothefuture.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AccountResponseInfoDto(
        @Schema(description = "은행 코드", example = "06")
        String code,

        @Schema(description = "은행명", example = "국민은행")
        String name,

        @Schema(description = "예금주명", example = "홍길동")
        String accountHolder,

        @Schema(description = "계좌 번호", example = "1234567890123456")
        String accountNumber
) {
}
