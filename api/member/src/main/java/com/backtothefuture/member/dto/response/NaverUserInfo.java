package com.backtothefuture.member.dto.response;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.StatusType;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record NaverUserInfo(
    @JsonProperty("resultcode")
    @NotNull(message = "resultCode 값은 필수입니다.")
    String resultCode,
    @JsonProperty("message")
    @NotNull(message = "message 값은 필수입니다.")
    String message,
    @JsonProperty("response")
    @NotNull(message = "naverResponse 객체는 필수입니다.")
    NaverResponse naverResponse
) {

    public Member toEntity(OAuthLoginDto dto, String randomPassword){
        return Member.builder()
            .authId(this.naverResponse.authId())
            .email(this.naverResponse.email())
            .password(randomPassword)
            .phoneNumber(this.naverResponse.phoneNumber())
            .name(this.naverResponse.name())
            .status(StatusType.ACTIVE)
            .provider(dto.getProviderType())
            .roles(dto.getRolesType())
            .build();
    }
}
