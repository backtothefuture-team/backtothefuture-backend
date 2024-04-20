package com.backtothefuture.store.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.dto.request.ProductUpdateDto;
import com.backtothefuture.store.dto.response.ProductGetOneResponseDto;
import com.backtothefuture.store.dto.response.ProductResponseDto;
import com.backtothefuture.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    // TODO: ROLE 을 STORE_OWNER, ADMIN 제한
    @Operation(summary = "새로운 상품 등록",
            description = "특정 가게에 새로운 상품을 등록합니다. 이미지는 'image/png', 'image/jpeg' 형식을 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "등록 성공",
                            content = @Content(schema = @Schema(implementation = BfResponse.class),
                                    examples = {
                                            @ExampleObject(name = "success", value = "{\"code\": 201, \"message\": \"정상적으로 생성되었습니다.\", \"data\": {\"product_id\": 1}}")
                                    }))
            })
    @PostMapping(
            value = "/stores/{storeId}/products",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BfResponse<?>> registerProduct(
            @PathVariable("storeId") Long storeId,
            @Parameter(description = "요청 정보입니다.")
            @Valid @RequestPart(value = "request") ProductRegisterDto productRegisterDto,
            @Parameter(description = "상품 이미지로 사용할 이미지를 첨부해 주세요.")
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("product_id", productService.registerProduct(storeId, productRegisterDto, thumbnail))));
    }

    // 상품 단건 조회 API
    @Operation(summary = "상품 정보 조회", description = "특정 가게의 특정 상품에 대한 자세한 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "storeId", description = "가게 ID", required = true),
                    @Parameter(name = "productId", description = "상품 ID", required = true)
            })
    @GetMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<BfResponse<ProductGetOneResponseDto>> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS, productService.getProduct(storeId, productId)));
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
    public ResponseEntity<BfResponse<Map<String, ProductResponseDto>>> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @Valid @RequestPart(value = "request") ProductUpdateDto productUpdateDto,
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BfResponse<>(SUCCESS,
                        Map.of("product",
                                productService.partialUpdateProduct(storeId, productId, productUpdateDto, thumbnail))));
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
