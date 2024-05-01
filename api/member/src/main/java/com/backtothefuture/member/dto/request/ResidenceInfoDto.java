package com.backtothefuture.member.dto.request;

public record ResidenceInfoDto(
        String address,                         // 거주지 주소

        double longitude,                       // 거주지 경도

        double latitude                        // 거주지 위도
) {
}
