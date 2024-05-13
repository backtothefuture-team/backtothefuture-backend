package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.DUPLICATED_HEART;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.NOT_FOUND_STORE_ID;

import com.backtothefuture.domain.heart.Heart;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.exception.MemberException;
import com.backtothefuture.store.exception.StoreException;
import com.backtothefuture.store.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void addHeart(UserDetailsImpl userDetails, Long storeId) {

        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        boolean exists = heartRepository.existsByMemberAndStore(member, store);
        if (exists) {
            throw new StoreException(DUPLICATED_HEART);
        }

        Heart heart = Heart.builder()
                .member(member)
                .store(store)
                .build();

        heartRepository.save(heart);
        store.addHeart();
    }

    @Transactional
    public void removeHeart(UserDetailsImpl userDetails, Long storeId) {

        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        heartRepository.deleteByMemberIdAndStoreId(member.getId(), storeId);
        store.removeHeart();
    }
}
