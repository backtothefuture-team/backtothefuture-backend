package com.backtothefuture.store.service;

import com.backtothefuture.domain.common.enums.ReviewErrorCode;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.review.Review;
import com.backtothefuture.domain.review.repository.ReviewRepository;
import com.backtothefuture.store.dto.request.ReviewCreateRequest;
import com.backtothefuture.store.exception.ReviewException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final S3Util s3Util;

    @Transactional
    public void save(Long memberId, ReviewCreateRequest request) {
        validateCreatable();

        Review review = request.toEntity(memberId);
        reviewRepository.save(review);

        String imageUrl = uploadReviewImage(review.getId(), request.imageFile());
        review.updateImageUrl(imageUrl);
    }

    private void validateCreatable() {
        boolean outOfDate = false; // TODO: 3일 이내에 결제한 리뷰인지 검증
        if (outOfDate) {
            throw new ReviewException(ReviewErrorCode.OUT_OF_DATE);
        }

        boolean exists = false; // TODO 이미 작성한 리뷰인지 검증
        if (exists) {
            throw new ReviewException(ReviewErrorCode.ALREADY_EXISTS);
        }
    }

    private String uploadReviewImage(Long reviewId, MultipartFile multipartFile) {
        try {
           return s3Util.uploadReviewImage(reviewId.toString(), multipartFile);

        } catch (IllegalArgumentException e) {
            throw new ReviewException(ReviewErrorCode.UNSUPPORTED_IMAGE_EXTENSION);

        } catch (IOException e) {
            throw new ReviewException(ReviewErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }
}
