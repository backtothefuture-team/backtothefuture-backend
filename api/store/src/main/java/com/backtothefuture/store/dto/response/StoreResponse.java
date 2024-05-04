package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.store.Store;
import java.time.LocalTime;

public record StoreResponse(
        Long id,
        String name,
        String image,

        //int surpriseBagAmount,
        //int surpriseBagPrice,
        //int surpriseBagDiscountRate, // TODO 서프라이즈백 구분 기능 확정된 후 구현

        double averageRating,
        int totalRatingCount,

        LocalTime startTime,
        LocalTime endTime

        // TODO 거리 표시
) {

    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getImage(),
                store.getAverageRating(),
                store.getTotalRatingCount(),
                store.getStartTime(),
                store.getEndTime()
        );
    }
}
