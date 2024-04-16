package com.backtothefuture.store.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDoneReservationResponseDto {

    private String storeImg; // 가게 이미지
    private String name; // 가게 이름
    private Long reservationId; // 마지막 조회 예약(주문) id
    private LocalDateTime reservationTime; // 예약 생성 시간
    private Integer totalPrice; // 주문 총 금액
    private List<Map<String,String>> productNames; // 상품 이름
}
