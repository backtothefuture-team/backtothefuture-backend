package com.backtothefuture.domain.member.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProviderType {
	KAKAO,
	NAVER,
	GOOGLE;

	@JsonCreator
	public static ProviderType fromRequest(String inputString) {
		for (ProviderType providerType : ProviderType.values()) {
			if (providerType.toString().equals(inputString.toUpperCase())) {
				return providerType;
			}
		}
		return null;
	}
}
