package com.backtothefuture.domain.common.enums;

import com.backtothefuture.domain.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OAuthErrorCode implements BaseErrorCode{
    NOT_MATCH_OAUTH_TYPE(400, "존재하지 않는 oauth 타입입니다.", HttpStatus.BAD_REQUEST),
    BAD_WEBCLIENT_REQUEST(500,"Webclient 요청이 잘못 되었습니다.",HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_OUTER_SYSTEM_ERROR(502,"외부 시스템 에러",HttpStatus.BAD_GATEWAY);

    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus status;

    OAuthErrorCode(int errorCode, String errorMessage, HttpStatus status) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    @Override
    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(this.errorCode, this.errorMessage);
    }
}
