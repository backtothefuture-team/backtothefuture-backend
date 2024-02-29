package com.backtothefuture.domain.common.enums;

import org.springframework.http.HttpStatus;

import com.backtothefuture.domain.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum MemberErrorCode implements BaseErrorCode {
	NOT_FIND_MEMBER_EMAIL(400, "존재하지 않는 회원입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATED_MEMBER_EMAIL(400, "이미 존재하는 회원 이메일입니다.", HttpStatus.BAD_REQUEST),
	DUPLICATED_MEMBER_PHONE_NUMBER(400, "이미 등록된 휴대폰 번호입니다.", HttpStatus.BAD_REQUEST),
	DELETE_MEMBER(400, "탈퇴 또는 삭제된 회원입니다.", HttpStatus.BAD_REQUEST),
	CHECK_ID_OR_PASSWORD(400, "아이디 또는 비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST),
	PASSWORD_NOT_MATCHED(400, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

	private final int errorCode;
	private final String errorMessage;
	private final HttpStatus status;

	MemberErrorCode(int errorCode, String message, HttpStatus status) {
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.status = status;
	}

	@Override
	public ErrorResponse getErrorResponse() {
		return new ErrorResponse(this.errorCode, this.errorMessage);
	}
}
