package com.backtothefuture.member.dto.response;

import lombok.Builder;

@Builder
public record LoginTokenDto(
	String accessToken,
	String refreshToken
) {
}
