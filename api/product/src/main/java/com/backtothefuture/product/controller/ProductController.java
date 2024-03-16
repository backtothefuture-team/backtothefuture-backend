package com.backtothefuture.product.controller;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.product.dto.request.ProductRegisterDto;
import com.backtothefuture.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @PostMapping("/{storeId}")
    public ResponseEntity<BfResponse<?>> registerProduct(
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody ProductRegisterDto productRegisterDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("product_id", productService.registerProduct(storeId, productRegisterDto))));
    }

    // 상품 삭제 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @DeleteMapping("/{storeId}/{productId}")
    public ResponseEntity<BfResponse<?>> deleteProduct(
            @PathVariable("storeId") Long storeId,
            @PathVariable("productId") Long productId
    ) {
        productService.deleteProduct(storeId, productId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
