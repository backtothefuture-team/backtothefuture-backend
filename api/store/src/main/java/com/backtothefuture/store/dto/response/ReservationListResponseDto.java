package com.backtothefuture.store.dto.response;


import java.time.LocalDateTime;

public record ReservationListResponseDto(
        String storeImg, // 가게 이미지
        String name, // 가게 이름
        Long reservationId,
        LocalDateTime reservationTime, // 예약 생성 시간
        Integer totalPrice // 주문 총 금액
) {
}
