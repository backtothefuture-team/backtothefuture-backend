package com.backtothefuture.member.controller;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.response.BankListResponseDto;
import com.backtothefuture.member.dto.response.TermListResponseDto;
import com.backtothefuture.member.service.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
@Tag(name = "약관 API", description = "약관 관련 API 입니다.")
public class TermController {

    private final TermService termService;

    @GetMapping("")
    @Operation(
            summary = "약관 리스트 조회",
            description = "약관 리스트 조회 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "약관 리스트 조회 성공 응답입니다.")
            })
    @SecurityRequirements(value = {}) // no security
    public ResponseEntity<BfResponse<TermListResponseDto>> getBanks() {
        return ResponseEntity.ok(new BfResponse<>(termService.getAllTerms()));
    }
}
