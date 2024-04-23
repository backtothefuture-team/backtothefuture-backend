package com.backtothefuture.store.dto.request;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.store.Store;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import lombok.Builder;

@Builder
@Schema(description = "상품 등록 정보(DTO)")
public record ProductRegisterDto(

        @NotBlank(message = "상품 이름은 필수 항목입니다.")
        @Size(max = 20, message = "상품 이름은 최대 20자 이하로 입력해주세요.")
        @Schema(description = "상품 이름", example = "크루아상", required = true)
        String name,

        @NotBlank(message = "상품 설명은 필수 항목입니다.")
        @Size(max = 50, message = "상품 설명은 최대 50자 이하로 입력해주세요.")
        @Schema(description = "상품 설명", example = "프랑스 고메버터가 풍부하게 들어간 프랑스식 패스트리", required = true)
        String description,

        @Min(value = 0, message = "가격은 음수일 수 없습니다.")
        @Max(value = 1000000, message = "최대 100만원까지 입력 가능합니다.")
        @Schema(description = "상품 가격 (단위: 원)", example = "3000", required = true)
        int price,

        @Min(value = 0, message = "재고는 음수일 수 없습니다.")
        @Schema(description = "재고 수량 (기본값: 0)", example = "50", required = false)
        int stockQuantity // 재고 수량, optional, default 0

) {
    public static Product toEntity(ProductRegisterDto productRegisterDto, Store store) {
        int stockQuantity = Optional.ofNullable(productRegisterDto.stockQuantity()).orElse(0);

        return Product.builder()
                .name(productRegisterDto.name())
                .description(productRegisterDto.description())
                .price(productRegisterDto.price())
                .stockQuantity(stockQuantity)
                .store(store)
                .build();
    }
}
