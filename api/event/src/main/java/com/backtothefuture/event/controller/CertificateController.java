package com.backtothefuture.event.controller;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.event.dto.request.VerifyCertificateRequest;
import com.backtothefuture.event.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certificate")
public class CertificateController {

    private final CertificateService certificationService;

    @GetMapping("/message/{phoneNumber}") // 인증 번호 발급
    public ResponseEntity<BfResponse<?>> getCertificateNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("certification_number",
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
