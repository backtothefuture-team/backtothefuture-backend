//package com.backtothefuture.event.service;
//
//import com.backtothefuture.domain.common.enums.CertificateErrorCode;
//import com.backtothefuture.domain.common.repository.RedisRepository;
//import com.backtothefuture.event.exception.CertificateException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
//import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
//import net.nurigo.sdk.message.exception.NurigoUnknownException;
//import net.nurigo.sdk.message.model.Message;
//import net.nurigo.sdk.message.model.MessageType;
//import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
//import net.nurigo.sdk.message.service.DefaultMessageService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class CoolsmsService {
//
//
//    @Value("${spring.certification.coolsms.fromPhoneNumber}")
//    private String fromPhoneNumber;
//
//    private final DefaultMessageService defaultMessageService;
//
//    private final RedisRepository repository;
//
//    public void sendCertificationMessage(String toPhoneNumber, String certificationNumber) {
//
//        Message message = new Message();
//
//        message.setTo(toPhoneNumber); // 수신 번호 세팅
//        message.setFrom(fromPhoneNumber); // 발신 번호 세팅
//        message.setType(MessageType.SMS); // 메시지 종류 설정 ( SMS, LMS, MMS ,,, )
//
//        try {
//            MultipleDetailMessageSentResponse response = defaultMessageService.send(message); // 메시지 전송
//        } catch (NurigoEmptyResponseException | NurigoMessageNotReceivedException |
//                 NurigoUnknownException ex) {
//            throw new CertificateException(CertificateErrorCode.MESSAGE_SEND_ERROR);
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//
//        // redis에 인증 번호 저장 ( key : 전화번호, value : 인증번호 )
//        repository.saveCertificationNumber(toPhoneNumber,certificationNumber);
//    }
//
//}
