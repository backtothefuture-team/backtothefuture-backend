package com.backtothefuture.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record TermListResponseDto(

        @Schema(description = "약관 리스트")
        List<TermDto> terms
) {
}
