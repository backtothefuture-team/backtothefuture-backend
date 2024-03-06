package com.backtothefuture.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccount(
    @JsonProperty("name")
    String name,

    @JsonProperty("email")
    String email,

    @JsonProperty("phone_number")
    String phoneNumber
) {

}
