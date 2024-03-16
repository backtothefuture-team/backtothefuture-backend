package com.backtothefuture.product.controller;

import com.backtothefuture.product.dto.request.ProductRegisterDto;
import com.backtothefuture.product.service.ProductService;
import com.backtothefuture.domain.response.BfResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{storeId}")
    public ResponseEntity<BfResponse<?>> registerProduct(
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody ProductRegisterDto productRegisterDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("product_id", productService.registerProduct(storeId, productRegisterDto))));
    }
}
