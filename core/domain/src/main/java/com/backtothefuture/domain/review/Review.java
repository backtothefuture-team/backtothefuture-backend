package com.backtothefuture.domain.review;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.store.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY) //
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;

    @Column(name = "store_id")
    private Long storeId;

    private Long memberId;

    // TODO 예약상품id

    private Double ratingCount;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Builder
    private Review(Long memberId, Long storeId, Double ratingCount, String content) {
        this.memberId = memberId;
        this.storeId = storeId;
        this.ratingCount = ratingCount;
        this.content = content;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
