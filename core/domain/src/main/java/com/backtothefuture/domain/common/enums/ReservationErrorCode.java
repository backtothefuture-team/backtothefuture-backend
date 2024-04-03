package com.backtothefuture.domain.common.enums;

import com.backtothefuture.domain.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode implements BaseErrorCode {

    NOT_ENOUGH_STOCK_QUANTITY(400, "재고가 충분하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_RESERVATION_PRODUCT(400, "주문 id에 해당하는 주문 상품이 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_VALID_RESERVATION_TIME(400, "적절한 예약 시간이 아닙니다.", HttpStatus.BAD_REQUEST);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    ReservationErrorCode(int errorCode, String errorMessage, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }

}