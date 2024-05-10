package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 재설정 요청 정보(DTO)")
public record MemberPasswordResetRequestDto(
        @Schema(description = "새 비밀번호", example = "password9876!", required = true, minLength = 6)
        @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
        @Size(min = 6, message = "비밀번호는 최소 6자 이상 입력해주세요.")
        String newPassword,

        @Schema(description = "새 비밀번호 확인", example = "password9876!", required = true)
        @NotBlank(message = "새 비밀번호 확인은 필수 항목입니다.")
        String newPasswordConfirm
) {
}
