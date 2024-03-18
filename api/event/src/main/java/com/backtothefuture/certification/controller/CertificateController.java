package com.backtothefuture.certification.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;

import com.backtothefuture.certification.dto.request.VerifyCertificateRequest;
import com.backtothefuture.certification.service.CertificateService;
import com.backtothefuture.domain.response.BfResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certificate")
public class CertificateController {

    private final CertificateService certificationService;

    @GetMapping("/message/{phoneNumber}") // 인증 번호 발급
    public ResponseEntity<BfResponse<?>> getCertificateNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new BfResponse<>(CREATE, Map.of("certification_id",
                certificationService.getCertificateNumber(phoneNumber))));
    }

    // TODO : 휴대폰 번호와 인증 번호를 pathvariable 형태로 보내도 되는가...?
    @PostMapping("/message") // 인증 번호 검증
    public ResponseEntity<BfResponse<?>> verifyCertificateNumber(
        @Valid @RequestBody VerifyCertificateRequest request) {
        certificationService.verifyCertificateNumber(request);
        return ResponseEntity.ok(new BfResponse<>(null));
    }
}
