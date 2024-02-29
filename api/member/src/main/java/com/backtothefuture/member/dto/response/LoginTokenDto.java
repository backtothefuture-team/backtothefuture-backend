package com.backtothefuture.member.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class LoginTokenDto {
	private String accessToken;

	@JsonIgnore
	private String refreshToken;
}
