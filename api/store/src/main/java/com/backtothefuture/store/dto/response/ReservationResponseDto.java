package com.backtothefuture.store.dto.response;

import lombok.Builder;

@Builder
public record ReservationResponseDto(
        String name, // 상품 이름
        int count,  // 상품의 주문 수량
        int price  // 주문에서 해당 상품의 총 가격
) {
}
