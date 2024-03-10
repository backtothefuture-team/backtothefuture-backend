package com.backtothefuture.store.exception;

import com.backtothefuture.domain.common.enums.StoreErrorCode;

import lombok.Getter;

@Getter
public class StoreException extends RuntimeException{
	private final StoreErrorCode errorCode;

	public StoreException(StoreErrorCode errorCode) {
		super(errorCode.getErrorMessage());
		this.errorCode = errorCode;
	}
}
