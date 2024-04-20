package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.GlobalErrorCode.CHECK_USER;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.FORBIDDEN_DELETE_PRODUCT;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_PRODUCT_ID;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_STORE_ID;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_STORE_PRODUCT_MATCH;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.UNSUPPORTED_IMAGE_EXTENSION;

import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.exception.CustomSecurityException;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.dto.request.ProductUpdateDto;
import com.backtothefuture.store.dto.response.ProductGetOneResponseDto;
import com.backtothefuture.store.dto.response.ProductResponseDto;
import com.backtothefuture.store.exception.ProductException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final S3Util s3Util;

    @Transactional
    public Long registerProduct(Long storeId, ProductRegisterDto productRegisterDto, MultipartFile thumbnail) {
        // 가게 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_STORE_ID));

        Product product = ProductRegisterDto.toEntity(productRegisterDto, store);
        Long id = productRepository.save(product).getId();

        // 이미지 업로드
        if (thumbnail != null) {
            try {
                String imageUrl = s3Util.uploadProductThumbnail(String.valueOf(storeId), String.valueOf(id), thumbnail);
                product.setThumbnailUrl(imageUrl);
            } catch (IllegalArgumentException e) {
                throw new ProductException(UNSUPPORTED_IMAGE_EXTENSION);
            } catch (IOException e) {
                throw new ProductException(IMAGE_UPLOAD_FAIL);
            }
        }

        return id;
    }

    @Transactional(readOnly = true)
    public ProductGetOneResponseDto getProduct(Long storeId, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT_ID));

        if (!storeId.equals(product.getStore().getId())) {
            throw new ProductException(NOT_FOUND_STORE_PRODUCT_MATCH);
        }

        return new ProductGetOneResponseDto(
                ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .thumbnail(product.getThumbnail())
                        .build());
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .thumbnail(product.getThumbnail())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto partialUpdateProduct(Long storeId, Long productId, ProductUpdateDto productUpdateDto,
                                                   MultipartFile thumbnail) {
        // 권한 검사
        if (!validateIsProductOwner(storeId, productId)) {
            throw new ProductException(FORBIDDEN_DELETE_PRODUCT);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT_ID));

        Optional.ofNullable(productUpdateDto.name())
                .ifPresent(product::updateName);
        Optional.ofNullable(productUpdateDto.description())
                .ifPresent(product::updateDescription);
        Optional.ofNullable(productUpdateDto.price())
                .ifPresent(product::updatePrice);
        Optional.ofNullable(productUpdateDto.stockQuantity())
                .ifPresent(product::updateStockQuantity);

        // thumbnail file 이 첨부되었을 경우 업데이트 한다.
        if (thumbnail != null) {
            try {
                String imageUrl = s3Util.uploadProductThumbnail(String.valueOf(storeId), String.valueOf(productId),
                        thumbnail);
                product.updateThumbnail(imageUrl);
            } catch (IllegalArgumentException e) {
                throw new ProductException(UNSUPPORTED_IMAGE_EXTENSION);
            } catch (IOException e) {
                throw new ProductException(IMAGE_UPLOAD_FAIL);
            }

        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .thumbnail(product.getThumbnail())
                .build();
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
