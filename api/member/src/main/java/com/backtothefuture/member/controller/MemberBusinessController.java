package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.request.BusinessNumberValidateRequestDto;
import com.backtothefuture.member.service.MemberBusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/business")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 관련 API 입니다.")
public class MemberBusinessController {

    private final MemberBusinessService memberBusinessService;

    @PostMapping("/info/validation")
    @Operation(summary = "사업자 정보 검증", description = "입력된 사업자 정보의 유효성을 검증합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사업자 정보 검증 유효성 검사 결과", useReturnTypeSchema = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "valid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": true}}"),
                                    @ExampleObject(name = "invalid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": false}}")
                            }
                    )
            )
    })
    public ResponseEntity<BfResponse<?>> validateBusinessNumber(
            @Valid @RequestBody BusinessInfoValidateRequestDto businessNumberValidateRequestDto) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessInfo(businessNumberValidateRequestDto))));
    }

    @PostMapping("/number/status")
    @Operation(summary = "사업자 번호 상태 조회", description = "입력된 사업자 정보의 유효성을 검증합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사업자 번호 검증 유효성 검사 결과", useReturnTypeSchema = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "valid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": true}}"),
                                    @ExampleObject(name = "invalid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": false}}")
                            }
                    )
            )
    })
    public ResponseEntity<BfResponse<?>> businessNumberStatus(
            @RequestBody BusinessNumberValidateRequestDto requestDto) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessNumber(requestDto.businessNumber()))));
    }
}
