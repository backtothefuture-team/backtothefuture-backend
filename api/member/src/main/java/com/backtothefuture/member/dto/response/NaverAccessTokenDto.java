package com.backtothefuture.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record NaverAccessTokenDto(
    @JsonProperty("access_token")
    @NotNull(message = "accessToken 값은 필수입니다.")
    String accessToken,
    @JsonProperty("refresh_token")
    String refreshToken,
    @JsonProperty("token_type")
    String tokenType,
    @JsonProperty("expires_in")
    Integer expiresIn,
    @JsonProperty("error")
    String error,
    @JsonProperty("error_description")
    String errorDescription
) {

}
