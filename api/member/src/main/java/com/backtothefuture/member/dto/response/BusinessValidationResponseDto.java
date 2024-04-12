package com.backtothefuture.member.dto.response;

import java.util.List;

public record BusinessValidationResponseDto(
        int requestCnt,
        String statusCode,
        List<ValidationResult> data
) {
    public record ValidationResult(
            String bNo,
            String valid,
            String validMsg
            // request_param 필드 필요 없어서 생략
    ) {
    }
}