package com.backtothefuture.member.controller;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.response.BankListResponseDto;
import com.backtothefuture.member.service.BankService;
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
@RequestMapping("/banks")
@Tag(name = "은행 API", description = "은행 관련 API 입니다.")
public class BankController {

    private final BankService bankService;

    @GetMapping("")
    @Operation(
            summary = "은행 리스트 조회",
            description = "은행 리스트 조회 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "은행 리스트 조회 성공 응답입니다.")
            })
    @SecurityRequirements(value = {}) // no security
    public ResponseEntity<BfResponse<BankListResponseDto>> getBanks() {
        return ResponseEntity.ok(new BfResponse<>(bankService.getAllBanks()));
    }
}
