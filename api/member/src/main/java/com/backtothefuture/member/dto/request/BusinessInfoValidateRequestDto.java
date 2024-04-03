package com.backtothefuture.member.dto.request;

import com.backtothefuture.member.annotation.ValidyyyyMMdd;
import jakarta.validation.constraints.NotBlank;

public record BusinessInfoValidateRequestDto(
        @NotBlank(message = "사업자번호는 필수 입니다.")
        String businessNumber, // 사업자번호

        @ValidyyyyMMdd
        String startDate, // 개업일자

        @NotBlank(message = "대표자 성명은 필수 입니다.")
        String name, // 대표자 성명

        String name2, // 대표자성명2
        String businessName, // 상호
        String corporationNumber, // 법인등록번호
        String businessSector, // 주업태명
        String businessType, // 주종목명
        String businessAddress // 사업장주소
) {
}