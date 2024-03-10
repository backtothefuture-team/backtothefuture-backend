package com.backtothefuture.store.exception;

import static com.backtothefuture.domain.common.enums.GlobalErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backtothefuture.domain.common.enums.BaseErrorCode;
import com.backtothefuture.domain.response.ErrorResponse;
import com.backtothefuture.security.exception.CustomSecurityException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error(">>>>> Internal Server Error : {}", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR.getErrorResponse());
	}

	/**
	 * Store Exception Handler
	 */
	@ExceptionHandler(StoreException.class)
	protected ResponseEntity<ErrorResponse> handleSecurityException(StoreException ex) {
		log.error(">>>>> StoreException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}

	/**
	 * Security Exception Handler
	 */
	@ExceptionHandler(CustomSecurityException.class)
	protected ResponseEntity<ErrorResponse> handleSecurityException(CustomSecurityException ex) {
		log.error(">>>>> SecurityException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}
}
