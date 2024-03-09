package com.backtothefuture.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record NaverResponse(
    @JsonProperty("id")
    @NotNull(message = "authId는 필수입니다.")
    String authId,
    @JsonProperty("name")
    @NotNull(message = "name는 필수입니다.")
    String name,
    @JsonProperty("email")
    @NotNull(message = "email는 필수입니다.")
    String email,
    @JsonProperty("mobile")
    @NotNull(message = "mobile는 필수입니다.")
    String phoneNumber
) {

}
