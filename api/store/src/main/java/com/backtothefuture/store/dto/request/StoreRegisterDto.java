package com.backtothefuture.store.dto.request;

import java.util.List;
import java.util.Optional;

import com.backtothefuture.domain.store.Store;
import com.backtothefuture.store.annotation.NumericStringList;

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

	@NumericStringList(message = "연락처는 숫자만 입력할 수 있습니다.")
	List<String> contact,

	String image
) {
	public static Store toEntity(StoreRegisterDto storeRegisterDto){
		String contact = Optional.ofNullable(storeRegisterDto.contact())
			.map(num ->String.join("-", num))
			.orElse("");

		String image = Optional.ofNullable(storeRegisterDto.image()).orElse("");

		return Store.builder()
			.name(storeRegisterDto.name())
			.description(storeRegisterDto.description())
			.location(storeRegisterDto.location())
			.contact(contact)
			.image(image)
			.build();
	}


}
