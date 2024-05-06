package com.backtothefuture.store.service;

import com.backtothefuture.domain.common.enums.ReviewErrorCode;
import com.backtothefuture.domain.common.enums.StoreErrorCode;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.review.Review;
import com.backtothefuture.domain.review.repository.ReviewRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.store.dto.request.ReviewCreateRequest;
import com.backtothefuture.store.dto.request.ReviewUpdateRequest;
import com.backtothefuture.store.dto.response.ReviewsReadResponse;
import com.backtothefuture.store.exception.ReviewException;
import com.backtothefuture.store.exception.StoreException;
import com.backtothefuture.store.repository.StoreRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final S3Util s3Util;

    @Transactional
    public void save(Long memberId, ReviewCreateRequest request) {
        validateCreatable();

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND_STORE_ID));

        Review review = request.toEntity(memberId, store);
        reviewRepository.save(review);

        updateStoreRating(review.getRating(), store);

        if (!request.imageFile().isEmpty()) {
            String imageUrl = uploadReviewImage(review.getId(), request.imageFile());
            review.updateImageUrl(imageUrl);
        }
    }

    private void updateStoreRating(double reviewRating, Store store) {
        store.updateRating(reviewRating);
        store.updateSortingIndex();
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

    public List<ReviewsReadResponse> findAllBy(Long memberId) {
        List<Review> reviews = reviewRepository.findAllByMemberId(memberId, Sort.by(Direction.DESC, "createdAt"));

        return ReviewsReadResponse.from(reviews);
    }

    @Transactional
    public void update(Long memberId, ReviewUpdateRequest request) {
        Review review = findById(request.reviewId());
        validateMemberMatch(review, memberId);

        if (request.imageFile() != null) {
            String imageUrl = uploadReviewImage(request.reviewId(), request.imageFile());
            review.update(request.ratingCount(), request.content(), imageUrl);

        } else {
            review.update(request.ratingCount(), request.content());
        }
    }

    @Transactional
    public void delete(Long memberId, Long reviewId) {
        Review review = findById(reviewId);
        validateMemberMatch(review, memberId);

        s3Util.deletePastReviewImage(reviewId.toString());

        reviewRepository.deleteById(reviewId);
    }

    private void validateMemberMatch(Review review, Long memberId) {
        if (!Objects.equals(review.getMemberId(), memberId)) {
            throw new ReviewException(ReviewErrorCode.MEMBER_MISMATCH);
        }
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.NOT_EXISTS));
    }
}
