package com.backtothefuture.store.dto.response;

import java.util.List;

public record ProductGetListResponseDto(
        List<ProductResponseDto> products
) {
}
