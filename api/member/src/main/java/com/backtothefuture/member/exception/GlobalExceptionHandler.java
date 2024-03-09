package com.backtothefuture.member.exception;

import static com.backtothefuture.domain.common.enums.GlobalErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backtothefuture.domain.common.enums.BaseErrorCode;
import com.backtothefuture.domain.common.enums.MemberErrorCode;
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
	 * Member RunTime Handler
	 */
	@ExceptionHandler(MemberException.class)
	protected ResponseEntity<ErrorResponse> handleMemberException(MemberException ex) {
		log.warn(">>>>> MemberException : {}", ex);
		MemberErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}

	/**
	 * Security 체크 관련
	 */
	@ExceptionHandler(CustomSecurityException.class)
	protected ResponseEntity<ErrorResponse> handleSecurityException(CustomSecurityException ex) {
		log.warn(">>>>> SecurityException : {}", ex);
		BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}

	/**
	 * oauth 예외 handler
	 */
	@ExceptionHandler(OAuthException.class)
	protected ResponseEntity<ErrorResponse> handleWebClientException(OAuthException ex) {
        log.error(">>>>> OAuthException : {}", ex);
        BaseErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity.status(errorCode.getStatus()).body(errorCode.getErrorResponse());
	}
}
