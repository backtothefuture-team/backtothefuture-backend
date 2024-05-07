package com.backtothefuture.store.dto.request;

import com.backtothefuture.domain.review.Review;
import com.backtothefuture.domain.store.Store;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ReviewCreateRequest(
        @NotNull(message = "상점ID는 필수 값입니다.")
        Long storeId,

        @NotNull(message = "별점은 필수 값입니다.")
        Double rating,

        String content,
        MultipartFile imageFile
        // TODO 예약상품 id

) {
    public Review toEntity(Long memberId, Store store) {
        return Review.builder()
                .memberId(memberId)
                .store(store)
                .rating(rating)
                .content(content)
                .build();
    }
}
