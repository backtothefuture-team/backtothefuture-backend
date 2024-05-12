package com.backtothefuture.domain.store;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    private Long sortingIndex;

    private String name;        // 가게 이름

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // 가게 설명

    private String location;    // 가게 위치

    private String contact;        // 연락처

    private String image;        // 가게 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;        // 회원

    private double averageRating;

    private int totalRatingCount;

    private LocalTime startTime; // 영업 시작 시간

    private LocalTime endTime; // 영업 종료 시간

    private double latitude;

    private double longitude;

    public void setThumbnailUrl(String url) {
        this.image = url;
    }

    public void updateRating(double reviewRating) {
        averageRating = Math.round(
                ((totalRatingCount * averageRating + reviewRating) / (totalRatingCount + 1)) * 10
        ) / 10.0;
        totalRatingCount++;
    }

    public void updateSortingIndex() {
        sortingIndex = makeSortingIndex(id, averageRating);
    }

    private static Long makeSortingIndex(Long storeId, double averageRating) {
        String format = (int) (averageRating * 10)
                + String.format("%010d", storeId);

        return Long.parseLong(format);
    }
}
