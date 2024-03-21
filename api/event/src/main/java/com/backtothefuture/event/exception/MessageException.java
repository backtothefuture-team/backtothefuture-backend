package com.backtothefuture.event.exception;

import com.backtothefuture.domain.common.enums.CertificateErrorCode;
import lombok.Getter;

@Getter
public class MessageException extends RuntimeException{

    private final CertificateErrorCode errorCode;

    public MessageException(CertificateErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
