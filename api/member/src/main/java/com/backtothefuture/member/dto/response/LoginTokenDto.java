package com.backtothefuture.member.dto.response;

public record LoginTokenDto(
	String accessToken,
	String refreshToken
) {
}
