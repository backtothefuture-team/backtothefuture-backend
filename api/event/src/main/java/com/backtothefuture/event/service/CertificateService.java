package com.backtothefuture.event.service;

import com.backtothefuture.domain.common.enums.CertificateErrorCode;
import com.backtothefuture.domain.common.repository.RedisRepository;
import com.backtothefuture.domain.common.util.RandomNumUtil;
import com.backtothefuture.event.dto.request.VerifyCertificateRequest;
import com.backtothefuture.event.exception.MessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CoolsmsService coolsmsService;

    private final RedisRepository redisRepository;

    public String getCertificateNumber(String phoneNumber) {
        String parsePhoneNum = parsePhoneNumber(phoneNumber); // 전화번호 parse
        String randomNum = RandomNumUtil.createRandomNum(6); // 6자리 임의의 숫자 생성

        coolsmsService.sendCertificationMessage(parsePhoneNum, randomNum);

        return randomNum;
    }

    public void verifyCertificateNumber(VerifyCertificateRequest request) {
        // 유효 번호 검증
        if (!validateCertificationNumber(request.getPhoneNumber(), request.certificationNumber()))
            throw new MessageException(CertificateErrorCode.INVALID_CERTIFCATE_NUMBER);

        redisRepository.deleteCertificationNumber(request.getPhoneNumber());
    }

    private String parsePhoneNumber(String phoneNumber) {
        String parseNum = phoneNumber.replace("-", "");

        if (!parseNum.matches("[0-9]+")) {
            throw new MessageException(CertificateErrorCode.INVALID_PHONE_NUMBER);
        }

        return parseNum; // 010-1234-1234 >> 01012341234 형태로 parse
    }

    private boolean validateCertificationNumber(String phoneNumber, String certificationNumber) {
        // 해당 key의 value 값이 존재 하는지 + 인증 번호가 일치 하는지
        return (redisRepository.hasKey(phoneNumber) && redisRepository.getCertificationNumber(
                phoneNumber).equals(certificationNumber));
    }
}