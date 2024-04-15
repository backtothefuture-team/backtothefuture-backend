package com.backtothefuture.member.exception;

import com.backtothefuture.domain.common.enums.MemberErrorCode;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {
	private final MemberErrorCode errorCode;

	public MemberException(MemberErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
