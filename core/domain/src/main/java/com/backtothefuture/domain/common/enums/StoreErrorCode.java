package com.backtothefuture.domain.common.enums;

import org.springframework.http.HttpStatus;

import com.backtothefuture.domain.response.ErrorResponse;

import lombok.Getter;

@Getter
public enum StoreErrorCode implements BaseErrorCode {
    CHECK_MEMBER(400, "회원의 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_STORE_NAME(400, "이미 존재하는 가게입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_HEART(400, "이미 찜한 상점입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_STORE_ID(404, "가게가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    UNSUPPORTED_IMAGE_EXTENSION(400, "지원하지 않는 확장자 입니다. jpeg혹은 png 파일을 업로드 해주세요.", HttpStatus.BAD_REQUEST),
    IMAGE_UPLOAD_FAIL(500, "이미지 업로드에 실패했습니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

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
