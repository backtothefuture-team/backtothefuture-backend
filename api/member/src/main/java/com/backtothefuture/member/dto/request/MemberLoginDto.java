package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDto {
    @Schema(description = "사용자의 이메일 주소", example = "user@example.com", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @Schema(description = "사용자의 비밀번호", example = "password123", required = true)
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상 입력해주세요.")
    private String password;
}
