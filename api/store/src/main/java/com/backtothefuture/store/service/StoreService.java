package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.StoreErrorCode.CHECK_MEMBER;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.DUPLICATED_STORE_NAME;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.UNSUPPORTED_IMAGE_EXTENSION;

import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.exception.StoreException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final S3Util s3Util;

    /**
     * 가게 등록
     */
    @Transactional
    public Long registerStore(UserDetailsImpl userDetails, StoreRegisterDto storeRegisterDto, MultipartFile thumbnail) {
        // 회원 조회
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new StoreException(CHECK_MEMBER));

        // 가게 중복 체크
        if (storeRepository.existsByName(storeRegisterDto.name())) {
            throw new StoreException(DUPLICATED_STORE_NAME);
        }

        // store 저장
        Store store = StoreRegisterDto.toEntity(storeRegisterDto, member);
        Long id = storeRepository.save(store).getId();

        // 이미지 업로드
        if (thumbnail != null) {
            try {
                String imageUrl = s3Util.uploadStoreThumbnail(String.valueOf(id), thumbnail);
                store.setThumbnailUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                throw new StoreException(UNSUPPORTED_IMAGE_EXTENSION);
            } catch (IOException e) {
                throw new StoreException(IMAGE_UPLOAD_FAIL);
            }
        }
        
        return id;
    }
}
