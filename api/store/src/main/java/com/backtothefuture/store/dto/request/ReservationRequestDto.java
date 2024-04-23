package com.backtothefuture.store.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@Builder
@Schema(description = "예약(주문)하기 요청 model입니다.")
public record ReservationRequestDto(
        @Schema(description = "가게 id 값입니다.")
        @NotNull(message = "가게 Id 값은 필수입니다.")
        Long storeId,
        @Schema(description = "예약 상품 값들입니다.")
        @Size(min = 1, message = "주문 품목은 최소 1개 이상입니다.")
        List<ReservationRequestItemDto> orderRequestItems,
        @Schema(description = "예약(주문) 요청 시간입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull(message = "예약 시간은 필수 입니다.")
        LocalDateTime reservationTime
) {

}
