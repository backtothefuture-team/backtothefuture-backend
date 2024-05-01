package com.backtothefuture.member.dto.request;

import com.backtothefuture.member.annotation.ValidyyyyMMdd;
import java.util.List;

public record MemberUpdateRequestDto(
        String name,                            // 닉네임

        List<String> phoneNumber,               // 전화번호

        List<Long> accpetedTerms,               // 약관 동의 ID 목록

        @ValidyyyyMMdd
        String birth,                           // 생년월일

        AccountInfoDto accountInfo,              // 계좌 정보
        
        ResidenceInfoDto residenceInfo           // 거주지 정보
) {
}
