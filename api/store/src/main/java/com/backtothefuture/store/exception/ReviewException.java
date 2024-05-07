package com.backtothefuture.store.exception;

import com.backtothefuture.domain.common.enums.ReviewErrorCode;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {
    private final ReviewErrorCode errorCode;

    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
