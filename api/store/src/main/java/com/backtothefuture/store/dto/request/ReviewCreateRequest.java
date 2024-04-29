package com.backtothefuture.store.dto.request;

import com.backtothefuture.domain.review.Review;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ReviewCreateRequest(
        @NotNull
        Long storeId,

        @NotNull
        Double ratingCount,

        String content,
        MultipartFile imageFile
        // TODO 예약상품 id

) {
    public Review toEntity(Long memberId) {
        return Review.builder()
                .memberId(memberId)
                .storeId(storeId)
                .ratingCount(ratingCount)
                .content(content)
                .build();
    }
}
