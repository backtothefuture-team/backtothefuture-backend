package com.backtothefuture.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "예약 상품 model입니다.")
public record ReservationRequestItemDto(
        @Schema(description = "상품 id 값입니다.")
        @NotNull(message = "상품 id 값은 필수입니다.")
        Long productId,
        @Schema(description = "주문 수량입니다.")
        @Min(value = 0, message = "주문 수량은 0보다 커야 합니다.")
        Integer quantity
) {
}
