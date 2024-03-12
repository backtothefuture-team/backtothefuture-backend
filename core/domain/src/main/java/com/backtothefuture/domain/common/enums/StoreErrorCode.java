package com.backtothefuture.domain.common.enums;

import org.springframework.http.HttpStatus;

import com.backtothefuture.domain.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum StoreErrorCode implements BaseErrorCode{
	CHECK_MEMBER(400, "회원의 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
	DUPLICATED_STORE_NAME(400, "이미 존재하는 가게입니다.", HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	StoreErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
