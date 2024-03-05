package com.backtothefuture.domain.common.repository;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
	@Value("${jwt.refresh-expiration-seconds}")
	private int refreshExp;

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * refresh token 저장
	 */
	public void saveToken(Long memberId, String refreshToken) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(refreshExp);
		valueOperations.set(String.valueOf(memberId), Map.of("refreshToken", refreshToken), expireDuration);
	}
}
