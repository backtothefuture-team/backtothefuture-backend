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

	@Value("${certification.message.expiration-seconds}")
	private int messageExp;

	@Value("${certification.mail.expiration-seconds}")
	private int mailExp;

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * refresh token 저장
	 */
	public void saveToken(Long memberId, String refreshToken) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(refreshExp);
		valueOperations.set(String.valueOf(memberId), Map.of("refreshToken", refreshToken), expireDuration);
	}

	/**
	 * 인증 번호 저장
	 */
	public void saveCertificationNumber(String phoneNumber, String certificationNumber) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(refreshExp);
		valueOperations.set(phoneNumber, certificationNumber, expireDuration);
	}

	/**
	 * 이메일 인증 번호 저장
	 */
	public void saveCertificationEmailNumber(String email, String certificationNumber) {
		ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(mailExp);
		valueOperations.set(email, certificationNumber, expireDuration);
	}

	/**
	 * 이메일 인증 유효시간 반환
	 */
	public int getMailExp() {
		return mailExp;
	}

	/**
	 * 인증 번호 가져 오기
	 */
	public String getCertificationNumber(String phoneNumber) {
		return (String) redisTemplate.opsForValue().get(phoneNumber);
	}

	/**
	 * 인증 번호 존재 조회
	 */
	public Boolean hasKey(String key){
		return redisTemplate.hasKey(key);
	}

	/**
	 * 인증 번호 삭제
	 */
	public void deleteCertificationNumber(String phoneNumber) {
		redisTemplate.delete(phoneNumber);
	}
}
