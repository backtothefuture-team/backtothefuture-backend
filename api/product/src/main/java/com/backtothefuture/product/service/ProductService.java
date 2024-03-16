package com.backtothefuture.product.service;

import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.product.dto.request.ProductRegisterDto;
import com.backtothefuture.domain.product.Product;
import com.backtothefuture.product.exception.ProductException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_STORE_ID;

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
}
