package com.backtothefuture.member.dto.response;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.StatusType;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record KakaoUserInfo(
    @JsonProperty("id")
    @NotNull(message = "authId는 필수입니다.")
    Long authId,
    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount
) {

    public Member toEntity(OAuthLoginDto dto, String randomPassword) {

        return Member.builder()
            .authId(String.valueOf(this.authId))
            .email(this.kakaoAccount.email())
            .password(randomPassword)
            .phoneNumber(this.kakaoAccount.phoneNumber())
            .name(this.kakaoAccount.name())
            .status(StatusType.ACTIVE)
            .provider(dto.getProviderType())
            .roles(dto.getRolesType())
            .build();
    }
}
