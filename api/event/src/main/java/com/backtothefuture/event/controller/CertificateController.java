package com.backtothefuture.event.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.domain.response.ErrorResponse;
import com.backtothefuture.event.dto.request.MailCertificateRequestDto;
import com.backtothefuture.event.dto.response.CertificateMailResponseDto;
import com.backtothefuture.event.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "certificate", description = "인증 API")
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

    @Operation(summary = "인증코드 메일 전송 API", description = "이메일로 인증코드를 전송합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 번호 발송 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이메일 형식에 맞지 않습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "invalid email", value = "{\"errorCode\": 400, \"errorMessage\": \"입력값에 대한 검증에 실패했습니다.\", \"validation\": {\"email\": \"이메일 형식에 맞지 않습니다.\"}}")
                            }
                    ))
    })
    @PostMapping(value = "/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<CertificateMailResponseDto>> sendCertificateMail(
            @Valid @RequestBody MailCertificateRequestDto mailCertificateRequestDto
    ) {
        CertificateMailResponseDto responseDto = certificationService.sendEmailCertificateNumber(
                mailCertificateRequestDto);

        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, responseDto));
    }

    @Operation(summary = "이메일 인증 번호 검증 API", description = "이메일과 인증 번호를 사용하여 인증 번호의 유효성을 검증합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 번호 유효성 검사 결과", useReturnTypeSchema = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "valid certification number", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": true}}"),
                                    @ExampleObject(name = "invalid certification number", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": false}}")
                            }
                    )
            )
    })
    @GetMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BfResponse<?>> verifyMailCertificationNumber(
            @Parameter(description = "이메일 주소", required = true) @RequestParam String email,
            @Parameter(description = "인증 번호", required = true) @RequestParam String certificationNumber
    ) {
        boolean isValid = certificationService.verifyCertificateEmailNumber(email, certificationNumber);
        return ResponseEntity.ok()
                .body(new BfResponse<>(SUCCESS, Map.of("isValid", isValid)));
    }
}
