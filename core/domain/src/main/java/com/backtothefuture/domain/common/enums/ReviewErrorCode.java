package com.backtothefuture.domain.common.enums;

import com.backtothefuture.domain.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements BaseErrorCode {
    // 400
    OUT_OF_DATE(400, "픽업일로부터 3일 이내에만 리뷰 작성이 가능합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS(400, "이미 작성한 리뷰입니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_IMAGE_EXTENSION(400, "지원하지 않는 확장자 입니다. jpeg혹은 png 파일을 업로드 해주세요.", HttpStatus.BAD_REQUEST),
    NOT_EXISTS(400, "존재하지 않는 리뷰입니다.", HttpStatus.BAD_REQUEST),

    // 401
    MEMBER_MISMATCH(401, "수정/삭제 권한이 없습니다.", HttpStatus.UNAUTHORIZED),

    // 500
    IMAGE_UPLOAD_FAIL(500, "이미지 업로드에 실패했습니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    ReviewErrorCode(int errorCode, String message, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }
}
