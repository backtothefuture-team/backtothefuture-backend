package com.backtothefuture.member.dto.request;

import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.member.annotation.EnumTypeMisMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "OAuthLoginDto", description = "소셜 로그인 요청 model입니다.")
public record OAuthLoginDto(
        @Schema(description = "어떤 종류의 소셜 로그인인지 식별 값입니다.", examples = {"KAKAO", "NAVER"})
        @EnumTypeMisMatch(message = "요청된 값이 존재하지 않거나 일치하는 provider type이 없습니다.")
        ProviderType providerType,
        @Schema(description = "회원 식별 값입니다.", examples = {"ROLE_ADMIN", "ROLE_USER", "ROLE_STORE_OWNER"})
        @EnumTypeMisMatch(message = "요청된 값이 존재하지 않거나 일치하는 roles type이 없습니다.")
        RolesType rolesType,
        @Schema(description = "네이버 소셜 로그인 시 state 값입니다. state 문자열 주시면 됩니다.", example = "state")
        @Nullable
        String state,
        @Schema(description = "인증 서버로부터 받은 access token 값입니다.")
        @NotBlank(message = "접근 토큰 값은 필수입니다.")
        String token
) {

}
