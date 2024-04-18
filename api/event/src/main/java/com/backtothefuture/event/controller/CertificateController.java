package com.backtothefuture.event.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.event.dto.request.MailCertificateRequestDto;
import com.backtothefuture.event.dto.response.CertificateMailResponseDto;
import com.backtothefuture.event.service.CertificateService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<BfResponse<CertificateMailResponseDto>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto
    ) {
        CertificateMailResponseDto responseDto = certificationService.sendEmailCertificateNumber(
                mailCertificateRequestDto);

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, responseDto));
    }

    @GetMapping("/email")
    public ResponseEntity<BfResponse<?>> verifyMailCertificationNumber(
            @RequestParam String email,
            @RequestParam String certificationNumber
    ) {
        boolean isValid = certificationService.verifyCertificateEmailNumber(email, certificationNumber);
        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, Map.of("isValid", isValid)));
    }
}
