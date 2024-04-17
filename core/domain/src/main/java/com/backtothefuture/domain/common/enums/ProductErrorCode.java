package com.backtothefuture.domain.common.enums;

import com.backtothefuture.domain.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProductErrorCode implements BaseErrorCode {

    // 400 BAD_REQUEST
    NOT_FOUND_STORE_ID(400, "존재하지 않는 가게 ID 입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PRODUCT_ID(400, "존재하지 않는 상품 ID 입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_STORE_PRODUCT_MATCH(400, "요청한 가게 ID, 상품 ID와 일치하는 정보가 없습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_IMAGE_EXTENSION(400, "지원하지 않는 확장자 입니다. jpeg혹은 png 파일을 업로드 해주세요.", HttpStatus.BAD_REQUEST),

    // 403 FORBIDDEN
    FORBIDDEN_DELETE_PRODUCT(403, "해당 상품을 삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_UPDATE_PRODUCT(403, "해당 상품정보를 수정 할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 500 INTERNAL_SERVER_ERROR
    IMAGE_UPLOAD_FAIL(500, "이미지 업로드에 실패했습니다. 관리자에게 문의해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    ProductErrorCode(int errorCode, String message, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }
}
