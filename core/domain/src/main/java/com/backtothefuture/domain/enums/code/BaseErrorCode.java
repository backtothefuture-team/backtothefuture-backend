package com.backtothefuture.domain.enums.code;

import org.springframework.http.HttpStatus;

import com.backtothefuture.domain.response.ErrorResponse;

public interface BaseErrorCode {
	Integer getErrorCode();

	String getErrorMessage();

	HttpStatus getStatus();

	ErrorResponse getErrorResponse();
}
