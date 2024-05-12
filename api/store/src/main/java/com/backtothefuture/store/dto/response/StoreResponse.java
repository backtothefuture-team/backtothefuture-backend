package com.backtothefuture.store.dto.response;

import com.backtothefuture.domain.store.Store;
import java.text.DecimalFormat;
import java.time.LocalTime;

public record StoreResponse(
        Long id,
        Long sortingIndex,

        String name,
        String image,

        //int surpriseBagAmount,
        //int surpriseBagPrice,
        //int surpriseBagDiscountRate, // TODO 서프라이즈백 구분 기능 확정된 후 구현

        double averageRating,
        int totalRatingCount,

        LocalTime startTime,
        LocalTime endTime,

        Double distance
) {

    public StoreResponse(Long id, Long sortingIndex, String name, String image, double averageRating,
                         int totalRatingCount, LocalTime startTime, LocalTime endTime, Double distance
    ) {
        this.id = id;
        this.sortingIndex = sortingIndex;
        this.name = name;
        this.image = image;
        this.averageRating = averageRating;
        this.totalRatingCount = totalRatingCount;
        this.startTime = startTime;
        this.endTime = endTime;
        if (distance == null) {
            this.distance = null;
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            this.distance = Double.valueOf(df.format(distance));
        }
    }


    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getSortingIndex(),
                store.getName(),
                store.getImage(),
                store.getAverageRating(),
                store.getTotalRatingCount(),
                store.getStartTime(),
                store.getEndTime(),
                null
        );
    }
}
