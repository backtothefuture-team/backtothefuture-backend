package com.backtothefuture.store.service;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.exception.CustomSecurityException;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.exception.ProductException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.backtothefuture.domain.common.enums.GlobalErrorCode.CHECK_USER;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Long registerProduct(Long storeId, ProductRegisterDto productRegisterDto) {
        // 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_STORE_ID));

        Product product = ProductRegisterDto.toEntity(productRegisterDto, store);
        return productRepository.save(product).getId();
    }

    @Transactional
    public void deleteProduct(Long storeId, Long productId) {
        // 권한 검사
        if (!validateIsProductOwner(storeId, productId)) {
            throw new ProductException(FORBIDDEN_DELETE_PRODUCT);
        }
        productRepository.deleteById(productId);
    }

    /**
     * 로그인 된 유저와 상품을 소유한 가게의 유저가 일치하는지 검증
     */
    boolean validateIsProductOwner(Long storeId, Long productId) {
        // 로그인 된 유저 조회
        UserDetailsImpl userDetails = (UserDetailsImpl) getUserDetails();
        Long requestedMemberId = userDetails.getId();

        // 상품을 소유한 가게의 memberId 조회
        Long productsOwnerMemberId = productRepository.findMemberIdByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_STORE_PRODUCT_MATCH));

        // 로그인 된 유저, 상품 소유자 memberId 비교
        if (!requestedMemberId.equals(productsOwnerMemberId)) {
            return false;
        }

        return true;
    }

    /**
     * 인증된 사용자 조회
     */
    // TODO: custom @Authenticationprincipal 구현
    private UserDetails getUserDetails() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(UserDetailsImpl.class::cast)
                .orElseThrow(() -> new CustomSecurityException(CHECK_USER));
    }
}
