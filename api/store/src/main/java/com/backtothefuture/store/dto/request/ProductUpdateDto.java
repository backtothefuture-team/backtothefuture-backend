package com.backtothefuture.store.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProductUpdateDto(
        @Size(max = 20, message = "상품 이름은 최대 20자 이하로 입력해주세요.")
        String name,

        @Size(max = 50, message = "상품 설명은 최대 50자 이하로 입력해주세요.")
        String description,

        @Min(value = 0, message = "가격은 음수일 수 없습니다.")
        @Max(value = 1000000, message = "최대 100만원까지 입력 가능합니다.")
        int price,

        @Min(value = 0, message = "재고는 음수일 수 없습니다.")
        int stockQuantity // 재고 수량, optional, default 0
) {

}
