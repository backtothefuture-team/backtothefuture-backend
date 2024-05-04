package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.review.Review;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewsReadResponse(
        Long id,
        String storeName,
        Double ratingCount,
        LocalDateTime createdAt,
        String imageUrl,
        String content
) {

    public static List<ReviewsReadResponse> from(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewsReadResponse::from)
                .toList();
    }

    private static ReviewsReadResponse from(Review review) {
        return new ReviewsReadResponse(
                review.getId(),
                review.getStore().getName(),
                review.getRatingCount(),
                review.getCreatedAt(),
                review.getImageUrl(),
                review.getContent()
        );
    }
}
