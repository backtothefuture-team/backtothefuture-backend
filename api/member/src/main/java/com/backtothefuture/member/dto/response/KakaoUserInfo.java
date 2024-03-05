package com.backtothefuture.member.dto.response;


import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.StatusType;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfo {

    @JsonProperty("id")
    @NotNull(message = "authId는 필수입니다.")
    private Long authId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public Member toEntity(OAuthLoginDto dto, String randomPassword){

        return Member.builder()
            .authId(String.valueOf(this.authId))
            .email(this.kakaoAccount.getEmail())
            .password(randomPassword)
            .phoneNumber(this.kakaoAccount.getPhoneNumber())
            .name(this.kakaoAccount.getName())
            .status(StatusType.ACTIVE)
            .provider(dto.getProviderType())
            .roles(dto.getRolesType())
            .build();
    }
}
