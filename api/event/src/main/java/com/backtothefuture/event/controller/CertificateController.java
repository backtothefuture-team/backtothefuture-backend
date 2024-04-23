package com.backtothefuture.event.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.event.dto.request.MailCertificateRequestDto;
import com.backtothefuture.event.service.CertificateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Tag(name = "인증 API", description = "인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/certificate")
@Slf4j
public class CertificateController {

    private final CertificateService certificationService;

//    @PostMapping("/message/{phoneNumber}") // 인증 번호 발급
//    public ResponseEntity<BfResponse<?>> getCertificateNumber(@PathVariable("phoneNumber") String phoneNumber) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new BfResponse<>(CREATE, Map.of("certification_number",
//                        certificationService.getCertificateNumber(phoneNumber))));
//    }
//
//    // TODO : 휴대폰 번호와 인증 번호를 pathvariable 형태로 보내도 되는가...?
//    @PostMapping("/message") // 인증 번호 검증
//    public ResponseEntity<BfResponse<?>> verifyCertificateNumber(
//            @Valid @RequestBody VerifyCertificateRequestDto request) {
//        certificationService.verifyCertificateNumber(request);
//        return ResponseEntity.ok(new BfResponse<>(null));
//    }

    @PostMapping("/email")
    public ResponseEntity<BfResponse<?>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto
    ) {
        int mailExp = certificationService.sendEmailCertificateNumber(mailCertificateRequestDto);

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, Map.of("mail_expiration_seconds", mailExp)));
    }

    // TODO: html 버튼, form 전송으로 변경할것
    @GetMapping("/email")
    public ModelAndView verifyCertificateMail(
            @RequestParam String email,
            @RequestParam String certificationNumber
    ) {
        certificationService.verifyCertificateEmailNumber(email, certificationNumber);
        // 인증 성공 시 성공 페이지를 반환
        ModelAndView successModelAndView = new ModelAndView("mail/verify-success");
        return successModelAndView;
    }

    @GetMapping("/email/{email}/status")
    public ResponseEntity<BfResponse<?>> checkCertificateEmailStatus(@PathVariable String email) {
        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS,
                        Map.of("is_certificated", certificationService.getCertificateEmailStatus(email))));
    }
}
