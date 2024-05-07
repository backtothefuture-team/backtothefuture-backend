package com.backtothefuture.member.dto.response;

import com.backtothefuture.domain.bank.Bank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "은행 리스트 응답 Dto", description = "은행 리스트(코드, 이름) 반환")
@Data
@AllArgsConstructor
public class BankListResponseDto {
    private List<BankDto> bankList;

    // Bank 엔티티 리스트를 받아 DTO 리스트로 변환
    public static BankListResponseDto fromBanks(List<Bank> bankEntities) {
        List<BankDto> bankDtos = bankEntities.stream()
                .map(bank -> new BankDto(bank.getCode(), bank.getName()))
                .collect(Collectors.toList());
        return new BankListResponseDto(bankDtos);
    }

    // 이미 BankDto 리스트가 있는 경우 사용할 수 있는 생성자
    public static BankListResponseDto fromBankDtos(List<BankDto> bankDtos) {
        return new BankListResponseDto(bankDtos);
    }
}
