package com.backtothefuture.domain.common.enums;

import org.springframework.http.HttpStatus;

import com.backtothefuture.domain.response.ErrorResponse;

public interface BaseErrorCode {
	int getErrorCode();

	String getErrorMessage();

	HttpStatus getStatus();

	ErrorResponse getErrorResponse();
}
