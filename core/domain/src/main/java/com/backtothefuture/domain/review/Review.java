package com.backtothefuture.domain.review;

import com.backtothefuture.domain.common.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private Long memberId;
    private Long storeId;
    // TODO 예약상품id

    private int ratingCount;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Builder
    private Review(Long memberId, Long storeId, int ratingCount, String content) {
        this.memberId = memberId;
        this.storeId = storeId;
        this.ratingCount = ratingCount;
        this.content = content;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
