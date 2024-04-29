package com.backtothefuture.domain.review.repository;

import com.backtothefuture.domain.review.Review;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByMemberId(Long memberId, Sort sort);
}
