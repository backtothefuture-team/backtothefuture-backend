package com.backtothefuture.domain.common.repository;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.util.concurrent.TimeUnit;

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
	 * 비트 값을 설정하고 만료 시간을 지정
	 *
	 * @param key    Redis 키
	 * @param offset 비트 위치
	 * @param value  설정할 비트 값
	 * @param ttl    만료 시간 (초 단위)
	 */
	protected void setBitWithExpiration(String key, long offset, boolean value, int ttl) {
		redisTemplate.opsForValue().setBit(key, offset, value);
		redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
	}

	/**
	 * 비트 값 조회
	 *
	 * @param key    Redis 키
	 * @param offset 비트 위치
	 * @return 비트 값 (boolean)
	 */
	protected boolean getBit(String key, long offset) {
		Boolean value = redisTemplate.opsForValue().getBit(key, offset);
		return value != null && value;
	}

	public void setMailCertificationFlag(String email) {
		String mailCertificatedKey = getMailCertificationKey(email);
		setBitWithExpiration(mailCertificatedKey, 0, true, mailExp);
	}

	public Boolean getMailCertificationFlag(String email) {
		String mailCertificatedKey = getMailCertificationKey(email);
		return getBit(mailCertificatedKey, 0);
	}

	public void deleteMailCertificationFlag(String email) {
		String mailCertificatedKey = getMailCertificationKey(email);
		redisTemplate.delete(mailCertificatedKey);
	}

	protected String getMailCertificationKey(String email) {
		return "email" + ":" + email + "verifyFlag";
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
