package com.backtothefuture.domain.review.repository;

import com.backtothefuture.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
