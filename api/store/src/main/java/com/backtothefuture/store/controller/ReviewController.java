package com.backtothefuture.store.controller;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ReviewCreateRequest;
import com.backtothefuture.store.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
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
}
