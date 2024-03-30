package com.backtothefuture.store.exception;

import com.backtothefuture.domain.common.enums.ReservationErrorCode;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final ReservationErrorCode errorCode;

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
