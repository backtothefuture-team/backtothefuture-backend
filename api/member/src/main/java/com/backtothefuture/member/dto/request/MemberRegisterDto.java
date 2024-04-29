package com.backtothefuture.member.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import com.backtothefuture.member.annotation.NumericStringList;

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
@Schema(description = "회원 가입 정보(DTO)")
public class MemberRegisterDto {
    @Schema(description = "사용자의 이메일 주소", example = "user@example.com", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동", required = true, minLength = 2, maxLength = 50)
    @NotBlank(message = "이름은 필수 항목입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "비밀번호", example = "password123", required = true, minLength = 6)
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상 입력해주세요.")
    private String password;

    @Schema(description = "비밀번호 확인", example = "password123", required = true, minLength = 6)
    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호 확인은 최소 6자 이상 입력해주세요.")
    private String passwordConfirm;

    @ArraySchema(arraySchema = @Schema(description = "회원 핸드폰 번호", example = "[\"010\", \"0000\", \"0000\"]"))
    @Size(min = 3, max = 3, message = "핸드폰 번호를 올바르게 입력해 주세요.")
    @NumericStringList(message = "핸드폰 번호는 숫자만 입력할 수 있습니다.")
    private List<String> phoneNumber;

    @Schema(description = "약관 동의 ID 목록. 필수 약관을 모두 동의해야 회원 가입이 가능합니다.", example = "[1, 2, 3, 4, 5]", required = true)
    @NotEmpty(message = "약관 동의는 필수 항목입니다.")
    private List<Long> accpetedTerms;

    public String getPhoneNumber() {
        return String.join("-", this.phoneNumber);
    }
}
