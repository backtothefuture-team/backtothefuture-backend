package com.backtothefuture.member.dto.request;

import java.util.List;

import com.backtothefuture.member.annotation.NumericStringList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class MemberRegisterDto {
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
	@NotBlank(message = "이메일은 필수 항목입니다.")
	private String email;

	@NotBlank(message = "이름은 필수 항목입니다.")
	@Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
	private String name;

	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Size(min = 6, message = "비밀번호는 최소 6자 이상 입력해주세요.")
	private String password;

	@NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
	@Size(min = 6, message = "비밀번호 확인은 최소 6자 이상 입력해주세요.")
	private String passwordConfirm;

	@Size(min = 3, max = 3, message = "핸드폰 번호를 올바르게 입력해 주세요.")
	@NumericStringList(message = "핸드폰 번호는 숫자만 입력할 수 있습니다.")
	private List<String> phoneNumber;

	public String getPhoneNumber() {
		return String.join("-", this.phoneNumber);
	}
}
