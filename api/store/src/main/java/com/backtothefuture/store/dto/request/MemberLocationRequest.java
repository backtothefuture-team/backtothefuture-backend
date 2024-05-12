package com.backtothefuture.store.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberLocationRequest(
        @NotNull(message = "사용자 현재 위치의 위도 값은 필수입니다.")
        double latitude,

        @NotNull(message = "사용자 현재 위치의 경도 값은 필수입니다.")
        double longitude
) {
}
