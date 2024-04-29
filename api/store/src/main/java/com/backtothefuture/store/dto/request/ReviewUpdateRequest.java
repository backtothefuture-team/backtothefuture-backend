package com.backtothefuture.store.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ReviewUpdateRequest(
        @NotNull
        Long reviewId,

        Double ratingCount,
        String content,
        MultipartFile imageFile
) {
}
