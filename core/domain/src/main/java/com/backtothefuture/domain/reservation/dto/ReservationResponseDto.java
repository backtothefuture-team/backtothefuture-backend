package com.backtothefuture.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationResponseDto {

    private String name; // 상품 이름
    private int count;  // 상품의 주문 수량
    private int price;  // 주문에서 해당 상품의 총 가격

    public void calculateTotalPrice() {
        this.price = price * count;
    }

}
