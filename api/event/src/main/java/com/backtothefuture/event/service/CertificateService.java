package com.backtothefuture.event.service;

import com.backtothefuture.domain.common.enums.CertificateErrorCode;
import com.backtothefuture.domain.common.repository.RedisRepository;
import com.backtothefuture.domain.common.util.RandomNumUtil;
import com.backtothefuture.event.dto.request.MailCertificateRequestDto;
import com.backtothefuture.event.dto.request.VerifyCertificateRequestDto;
import com.backtothefuture.event.exception.CertificateException;
import com.backtothefuture.event.exception.VerifyMailFailException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CoolsmsService coolsmsService;

    private final RedisRepository redisRepository;

    private final MailService mailService;

    @Value("${spring.certification.mail.baseurl}")
    private String baseUrl;

    public String getCertificateNumber(String phoneNumber) {
        String parsePhoneNum = parsePhoneNumber(phoneNumber); // 전화번호 parse
        String randomNum = RandomNumUtil.createRandomNum(6); // 6자리 임의의 숫자 생성

        coolsmsService.sendCertificationMessage(parsePhoneNum, randomNum);

        return randomNum;
    }

    public void verifyCertificateNumber(VerifyCertificateRequestDto request) {
        // 유효 번호 검증
        if (!validateCertificationNumber(request.getPhoneNumber(), request.certificationNumber()))
            throw new CertificateException(CertificateErrorCode.INVALID_CERTIFCATE_NUMBER);

        redisRepository.deleteCertificationNumber(request.getPhoneNumber());
    }

    private String parsePhoneNumber(String phoneNumber) {
        String parseNum = phoneNumber.replace("-", "");

        if (!parseNum.matches("[0-9]+")) {
            throw new CertificateException(CertificateErrorCode.INVALID_PHONE_NUMBER);
        }

        return parseNum; // 010-1234-1234 >> 01012341234 형태로 parse
    }

    private boolean validateCertificationNumber(String phoneNumber, String certificationNumber) {
        // 해당 key의 value 값이 존재 하는지 + 인증 번호가 일치 하는지
        return (redisRepository.hasKey(phoneNumber) && redisRepository.getCertificationNumber(
                phoneNumber).equals(certificationNumber));
    }

    public int sendEmailCertificateNumber(MailCertificateRequestDto mailCertificateRequestDto) {
        String email = mailCertificateRequestDto.email();

        // 랜덤 번호 발급
        String randomNumber = RandomNumUtil.createRandomNum(6); // 6자리 임의의 숫자 생성

        // 인증 url 설정
        String certificateUrl = String.format("%s?certificationNumber=%s&email=%s",
                getServerUrl(), randomNumber, email);

        // 메일 내용 설정
        HashMap<String, String> content = new HashMap<>();
        content.put("subject", "메일 제목");
        content.put("text", "메일 내용, 인증 url = " + certificateUrl);

        // 메일 전송
        try {
            mailService.sendMail(email, content);
        } catch (MessagingException e) {
            throw new CertificateException(CertificateErrorCode.MAIL_SEND_ERROR);
        }

        // 임시 발급 번호 redis에 저장
        redisRepository.saveCertificationEmailNumber(email, randomNumber);

        return redisRepository.getMailExp();
    }

    public void verifyCertificateEmailNumber(String email, String certificationNumber) {
        // 유효 번호 검증
        if (!validateCertificationNumber(email, certificationNumber))
            throw new VerifyMailFailException(CertificateErrorCode.INVALID_CERTIFCATE_NUMBER);

        // 임시 인증 번호 삭제
        redisRepository.deleteCertificationNumber(email);

        // 검증 여부 저장
        redisRepository.setMailCertificationFlag(email);
    }

    public boolean getCertificateEmailStatus(String email) {
        return redisRepository.getMailCertificationFlag(email);
    }

    public String getServerUrl() {
        return baseUrl + "/certificate/email";
    }
}