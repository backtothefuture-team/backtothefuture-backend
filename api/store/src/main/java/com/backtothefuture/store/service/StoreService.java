package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.StoreErrorCode.CHECK_MEMBER;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.DUPLICATED_STORE_NAME;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.UNSUPPORTED_IMAGE_EXTENSION;

import com.backtothefuture.domain.common.enums.StoreErrorCode;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.domain.SortingOption;
import com.backtothefuture.store.dto.request.MemberLocationRequest;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.dto.response.StoreDetailResponse;
import com.backtothefuture.store.dto.response.StoreResponse;
import com.backtothefuture.store.exception.StoreException;
import com.backtothefuture.store.repository.StorePagingRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StorePagingRepository storePagingRepository;
    private final S3Util s3Util;

    /**
     * 가게 등록
     */
    // TODO 가게 등록 시 가게 주소 기반 위도 경도 저장 필요
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

    @Transactional(readOnly = true)
    public List<StoreResponse> findStores(
            SortingOption sortingOption, Long sortingIndex, Long cursor,
            Integer size, Integer page, MemberLocationRequest request
    ) {
        Pageable pageable = Pageable.ofSize(size);

        List<Store> stores = new ArrayList<>();

        switch (sortingOption) {
            case DISTANCE:
                return storePagingRepository.findStoresByLocation(
                        request.latitude(),
                        request.longitude(),
                        PageRequest.of(page, size)
                );

            case STAR:
                stores = storePagingRepository.findStoresBySortingIndex(sortingIndex, pageable);
                break;

            case DEFAULT:
                stores = storePagingRepository.findStoresByCursor(cursor, pageable);
        }

        return stores.stream()
                .map(StoreResponse::from)
                .toList();
    }

    public StoreDetailResponse findStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.NOT_FOUND_STORE_ID));

        List<Product> products = productRepository.findAllByStoreId(storeId);

        return StoreDetailResponse.from(store, products);
    }
}
