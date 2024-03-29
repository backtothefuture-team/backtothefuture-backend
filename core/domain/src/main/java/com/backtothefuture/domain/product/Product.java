package com.backtothefuture.domain.product;

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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends MutableBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;                // 상품 아이디

    private String name;            // 상품 이름

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;     // 상품 설명

    private int price;              // 상품 가격

    private int stockQuantity;      // 재고 수량

    private String thumbnail;       // 썸네일 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;            // 가게

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int updateStockWhenReserve(int stockQuantity) {
        this.stockQuantity-=stockQuantity;
        return stockQuantity * price; // 상품에 대한 총 주문 금액 반환
    }
}
