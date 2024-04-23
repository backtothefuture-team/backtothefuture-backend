package com.backtothefuture.member.dto.request;

import com.backtothefuture.member.annotation.ValidyyyyMMdd;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record BusinessInfoValidateRequestDto(

        @Schema(description = "사업자 번호", required = true, example = "123-45-67890")
        @NotBlank(message = "사업자번호는 필수 입니다.")
        String businessNumber, // 사업자번호

        @Schema(description = "개업 일자, yyyyMMdd 형식", example = "20220415")
        @ValidyyyyMMdd
        String startDate, // 개업일자

        @Schema(description = "대표자 성명", required = true, example = "홍길동")
        @NotBlank(message = "대표자 성명은 필수 입니다.")
        String name, // 대표자 성명

        @Schema(description = "대표자 성명2", example = "김철수")
        String name2, // 대표자성명2

        @Schema(description = "대표자 성명2", example = "김철수")
        String businessName, // 상호

        @Schema(description = "상호", example = "OOO 기업")
        String corporationNumber, // 법인등록번호

        @Schema(description = "주업태명", example = "제조업")
        String businessSector, // 주업태명

        @Schema(description = "주종목명", example = "전자제품")
        String businessType, // 주종목명

        @Schema(description = "사업장 주소", example = "서울시 강남구 역삼동 123-45")
        String businessAddress // 사업장주소
) {
}
