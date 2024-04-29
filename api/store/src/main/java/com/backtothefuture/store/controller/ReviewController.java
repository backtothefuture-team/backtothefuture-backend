package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ReviewCreateRequest;
import com.backtothefuture.store.dto.request.ReviewUpdateRequest;
import com.backtothefuture.store.dto.response.ReviewsReadResponse;
import com.backtothefuture.store.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<BfResponse<Void>> createReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @ModelAttribute ReviewCreateRequest request
    ) {
        reviewService.save(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<BfResponse<List<ReviewsReadResponse>>> readReviews(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<ReviewsReadResponse> response = reviewService.findAllBy(userDetails.getId());

        return ResponseEntity.ok(
                new BfResponse<>(SUCCESS, response)
        );
    }

    @PatchMapping("/reviews")
    public ResponseEntity<BfResponse<Void>> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @ModelAttribute ReviewUpdateRequest request
    ) {
        reviewService.update(userDetails.getId(), request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<BfResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reviewId
    ) {
        reviewService.delete(userDetails.getId(), reviewId);

        return ResponseEntity.ok().build();
    }
}
