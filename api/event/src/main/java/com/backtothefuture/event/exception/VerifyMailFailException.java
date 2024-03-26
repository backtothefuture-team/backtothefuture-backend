package com.backtothefuture.event.exception;

import com.backtothefuture.domain.common.enums.CertificateErrorCode;
import lombok.Getter;

@Getter
public class VerifyMailFailException extends RuntimeException{
    private final CertificateErrorCode errorCode;

    public VerifyMailFailException(CertificateErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
