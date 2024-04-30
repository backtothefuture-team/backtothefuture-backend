package com.backtothefuture.store.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ReviewUpdateRequest(
        @NotNull(message = "리뷰 식별자는 필수 값입니다.")
        Long reviewId,

        Double ratingCount,
        String content,
        MultipartFile imageFile
) {
}
