package com.backtothefuture.domain.reservation.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum OrderType {

    REGISTRATION, // 예약 접수
    CONFIRMATION, // 예약 확정
    PICKUP_WAITING, // 픽업 대기
    PICKUP_DONE; // 픽업 완료

    @JsonCreator
    public static OrderType fromRequest(String inputString) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.name().equals(inputString)) {
                return orderType;
            }
        }
        return null;
    }
}
