package com.backtothefuture.store.dto.response;

import lombok.Builder;

@Builder
public record ProductResponseDto(
        Long id,                // 상품 아이디
        String name,            // 상품 이름
        String description,     // 상품 설명
        int price,              // 상품 가격
        int stockQuantity,      // 재고 수량
        String thumbnail       // 썸네일 이미지
) {
}
