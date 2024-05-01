package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResidenceInfoDto(
        @Schema(description = "거주지 주소", example = "서울시 강남구 역삼동 123-45")
        String address,                         // 거주지 주소

        @Schema(description = "거주지 경도", example = "127.123456")
        double longitude,                       // 거주지 경도

        @Schema(description = "거주지 위도", example = "37.123456")
        double latitude                        // 거주지 위도
) {
}
