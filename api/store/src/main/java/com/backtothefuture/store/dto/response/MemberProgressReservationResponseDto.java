package com.backtothefuture.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주문 진행 응답 model 입니다.")
public class MemberProgressReservationResponseDto {
    @Schema(description = "가게 이미지입니다.")
    private String storeImg;
    @Schema(description = "가게 이름입니다.")
    private String name;
    @Schema(description = "가게 id 값입니다.")
    private Long reservationId;
    @Schema(description = "예약(주문) 접수 시간입니다.")
    private LocalDateTime reservationTime;
    @Schema(description = "주문 총 금액입니다.")
    private Integer totalPrice;
    @Schema(description = "예약(주문) 상품 이름 목록입니다.")
    private List<ReservationProductNameResponseDto> productNames;
    @Schema(description = "주문 진행 내역입니다.")
    private List<ProgressReservationHistoryResponseDto> reservationHistories;
}
