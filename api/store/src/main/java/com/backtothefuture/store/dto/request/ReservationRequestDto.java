package com.backtothefuture.store.dto.request;


import com.backtothefuture.domain.reservation.enums.TimeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

@Builder
public record ReservationRequestDto(

    @NotNull(message = "가게 Id 값은 필수입니다.")
    Long storeId,

    @Size(min = 1, message = "주문 품목은 최소 1개 이상입니다.")
    List<ReservationRequestItemDto> orderRequestItems,

    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "예약 시간은 필수 입니다.")
    LocalTime reservationTime,

    @NotNull(message = "시간 종류는 필수 정보입니다.")
    TimeType timeType
) {

}
