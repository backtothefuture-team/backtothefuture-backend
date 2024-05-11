package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.reservation.enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "주문 상태 이력 응답 model")
public record ProgressReservationHistoryResponseDto(
        @Schema(description = "주문 상태 종류")
        OrderType orderType,
        @Schema(description = "시간")
        LocalDateTime eventTime
) {
}
