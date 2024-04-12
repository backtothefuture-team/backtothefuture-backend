package com.backtothefuture.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BusinessStatusResponseDto(
        String statusCode,
        int matchCnt,
        int requestCnt,
        List<StatusResult> data
) {
    public record StatusResult(
            @JsonProperty("b_stt")
            String businessStatus, // 납세자상태 (명칭)
            @JsonProperty("b_stt_cd")
            String businessStatusCode // 납세자상태 (코드)
    ) {
    }
}
