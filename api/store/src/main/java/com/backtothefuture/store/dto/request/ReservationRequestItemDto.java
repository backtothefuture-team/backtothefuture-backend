package com.backtothefuture.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestItemDto(
        @NotNull(message = "상품 id 값은 필수입니다.")
        Long productId,

        @Min(value = 0, message = "주문 수량은 0보다 커야 합니다.")
        Integer quantity
) {
}
