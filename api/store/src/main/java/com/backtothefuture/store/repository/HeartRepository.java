package com.backtothefuture.store.repository;

import com.backtothefuture.domain.heart.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    void deleteByMemberIdAndStoreId(Long memberId, Long storeId);
}
