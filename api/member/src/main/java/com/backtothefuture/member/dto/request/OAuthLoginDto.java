package com.backtothefuture.member.dto.request;

import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.member.annotation.EnumTypeMisMatch;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record OAuthLoginDto(
    @NotBlank(message = "승인 코드는 필수입니다.")
    String authorizationCode,

    @EnumTypeMisMatch(message = "요청된 값이 존재하지 않거나 일치하는 provider type이 없습니다.")
    ProviderType providerType,

    @EnumTypeMisMatch(message = "요청된 값이 존재하지 않거나 일치하는 roles type이 없습니다.")
    RolesType rolesType,
    @Nullable
    String state
) {

}
