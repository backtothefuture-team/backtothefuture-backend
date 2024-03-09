package com.backtothefuture.member.dto.request;


import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class OAuthLoginDto {

    @NotBlank(message = "승인 코드는 필수입니다.")
    private String authorizationCode;

    @NotNull(message = "providerType은 필수입니다.")
    private ProviderType providerType;

    @NotNull(message = "rolesType은 필수입니다.")
    private RolesType rolesType;

    @Nullable
    private String state;

    /*
     * 의문사항: core 패키지의 Provider에 @JsonCreator를 이용하여 dto마다 @JsonCreator를 사용하고 싶지 않은데
     * api 패키지에 enum 패키지를 새로 만드는 것이 괜찮은지 의문 존재...
     */
    @JsonCreator
    public OAuthLoginDto(
        @JsonProperty("authorizationCode") String authorizationCode,
        @JsonProperty("providerType") ProviderType providerType,
        @JsonProperty("rolesType") RolesType rolesType,
        @JsonProperty("state") String state) {
        this.authorizationCode = authorizationCode;
        this.providerType = providerType;
        this.rolesType = rolesType;
        this.state = state;
    }
}
