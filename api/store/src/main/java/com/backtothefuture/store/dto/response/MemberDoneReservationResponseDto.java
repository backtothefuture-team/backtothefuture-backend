package com.backtothefuture.store.dto.response;



import java.util.List;
import lombok.Builder;

@Builder
public record MemberDoneReservationResponseDto(
        List<MemberDoneReservationListDto> memberDoneReservationList, // 상품 이름
        boolean isLast
) {
}
