package com.backtothefuture.store.dto.response;



import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "주문 완료 내역 조회 model입니다.")
public record MemberDoneReservationResponseDto(
        @Schema(description = "주문 정보 값들입니다.")
        List<MemberDoneReservationListDto> memberDoneReservationList,// 상품 이름
        @Schema(description = "다음 페이지 존재 여부입니다.")
        boolean isLast
) {
}
