package com.backtothefuture.store.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
@Builder
public record ReservationRequestDto(

        @NotNull(message = "가게 Id 값은 필수입니다.")
        Long storeId,

        @Size(min = 1, message = "주문 품목은 최소 1개 이상입니다.")
        List<ReservationRequestItemDto> orderRequestItems
) {
}
