package com.backtothefuture.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "완료/진행 중 주문 내역의 상품 이름 응답 model")
public record ReservationProductNameResponseDto(
        @Schema(description = "상품 이름")
        String productName
) {
}
