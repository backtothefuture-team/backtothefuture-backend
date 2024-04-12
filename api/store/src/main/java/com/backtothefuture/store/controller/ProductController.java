package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.dto.request.ProductUpdateDto;
import com.backtothefuture.store.service.ProductService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @PostMapping("/stores/{storeId}/products")
    public ResponseEntity<BfResponse<?>> registerProduct(
            @PathVariable("storeId") Long storeId,
            @Valid @RequestPart(value = "request") ProductRegisterDto productRegisterDto,
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("product_id", productService.registerProduct(storeId, productRegisterDto, thumbnail))));
    }

    // 상품 단건 조회 API
    @GetMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<BfResponse<?>> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS, Map.of("product", productService.getProduct(storeId, productId))));
    }

    // 모든 상품 조회 API
    // TODO: 정렬 기준, 페이지네이션 등 추가 ..
    @GetMapping("/products")
    public ResponseEntity<BfResponse<?>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS, Map.of("products", productService.getAllProducts())));
    }

    // 상품 수정 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @PatchMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<BfResponse<?>> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateDto productUpdateDto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS,
                        Map.of("product", productService.partialUpdateProduct(storeId, productId, productUpdateDto))));
    }

    // 상품 삭제 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @DeleteMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<BfResponse<?>> deleteProduct(
            @PathVariable("storeId") Long storeId,
            @PathVariable("productId") Long productId
    ) {
        productService.deleteProduct(storeId, productId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
