package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.store.Store;
import java.util.List;
import lombok.Builder;

public record StoreDetailResponse(
        StoreInfo storeInfo,
        List<ProductDto> products

) {
    public static StoreDetailResponse from(Store store, List<Product> products) {
        return new StoreDetailResponse(
                StoreInfo.from(store),
                products.stream()
                        .map(ProductDto::from)
                        .toList()
        );
    }

    @Builder
    private record StoreInfo(
            Long storeId,
            String description,
            String location,
            double averageRating,
            int totalRatingCount,
            int heartCount,
            boolean isHeart
            // TODO 픽업 시간, 알러지 정보
    ) {
        private static StoreInfo from(Store store) {
            return StoreInfo.builder()
                    .storeId(store.getId())
                    .description(store.getDescription())
                    .location(store.getLocation())
                    .averageRating(store.getAverageRating())
                    .totalRatingCount(store.getTotalRatingCount())
                    .heartCount(0) // TODO 가게 찜 시 스토어 필드의 찜 개수 증가하는 기능 구현
                    .isHeart(true) // TODO 로그인/비로그인 회원 구분 필요
                    .build();
        }
    }

    @Builder
    private record ProductDto(
            String name,
            int price,
            // TODO 상품 할인율
            String thumbnail

    ) {
        private static ProductDto from(Product product) {
            return ProductDto.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .thumbnail(product.getThumbnail())
                    .build();
        }
    }
}
