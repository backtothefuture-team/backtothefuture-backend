package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "약관 이력 업데이트 요청")
public record TermHistoryUpdateDto(
        @Schema(description = "약관 동의 여부", example = "true")
        boolean isAccepted
) {
}
