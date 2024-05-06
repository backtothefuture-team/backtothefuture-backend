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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Builder
@AllArgsConstructor
public class Review extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private Long memberId;

    // TODO 예약상품id

    private Double rating;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void update(Double rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public void update(Double rating, String content, String imageUrl) {
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
