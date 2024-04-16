package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.reservation.enums.OrderType;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MemberProgressReservationResponseDto(
        String storeImg, // 가게 이미지
        String name, // 가게 이름
        Long reservationId,
        LocalDateTime reservationTime, // 예약 생성 시간
        OrderType orderType, // 주문 진행 상황
        LocalTime eventTime // 주문 상태 시작 시간
) {
}
