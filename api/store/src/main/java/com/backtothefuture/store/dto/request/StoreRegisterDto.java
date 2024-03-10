package com.backtothefuture.store.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRegisterDto(
	@NotBlank(message = "가게이름은 필수 항목입니다.")
	@Size(max = 20, message = "가게이름은 최대 20자 이하로 입력해주세요.")
	String name,

	@NotBlank(message = "가게설명은 필수 항목입니다.")
	@Size(max = 50, message = "가게설명은 최대 50자 이하로 입력해주세요.")
	String description,

	@NotBlank(message = "가게위치는 필수 항목입니다.")
	@Size(max = 50, message = "가게위치는 최대 50자 이하로 입력해주세요.")
	String location,

	List<String> contact,

	String image
) {

}
