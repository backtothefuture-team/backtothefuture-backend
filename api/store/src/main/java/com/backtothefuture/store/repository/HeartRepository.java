package com.backtothefuture.store.repository;

import com.backtothefuture.domain.heart.Heart;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    void deleteByMemberIdAndStoreId(Long memberId, Long storeId);

    boolean existsByMemberAndStore(Member member, Store store);
}
