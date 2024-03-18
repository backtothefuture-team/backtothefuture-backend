package com.backtothefuture.certification.exception;


import static com.backtothefuture.domain.common.enums.GlobalErrorCode.*;

import com.backtothefuture.domain.common.enums.BaseErrorCode;
import java.security.cert.CertificateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.backtothefuture.domain.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error(">>>>> Internal Server Error : {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(INTERNAL_SERVER_ERROR.getErrorResponse());
    }

    @ExceptionHandler(MessageException.class)
    protected ResponseEntity<ErrorResponse> handleCertificationException(MessageException ex) {
        log.error(">>>>> Internal Server Error : {}", ex);
        BaseErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
    }
}

