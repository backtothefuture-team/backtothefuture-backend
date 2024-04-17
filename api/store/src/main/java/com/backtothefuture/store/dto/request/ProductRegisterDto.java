package com.backtothefuture.store.dto.request;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.store.Store;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import lombok.Builder;

@Builder
public record ProductRegisterDto(

        @NotBlank(message = "상품 이름은 필수 항목입니다.")
        @Size(max = 20, message = "상품 이름은 최대 20자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "상품 설명은 필수 항목입니다.")
        @Size(max = 50, message = "상품 설명은 최대 50자 이하로 입력해주세요.")
        String description,

        @Min(value = 0, message = "가격은 음수일 수 없습니다.")
        @Max(value = 1000000, message = "최대 100만원까지 입력 가능합니다.")
        int price,

        @Min(value = 0, message = "재고는 음수일 수 없습니다.")
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
