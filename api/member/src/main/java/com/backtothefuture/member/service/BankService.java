package com.backtothefuture.member.service;

import com.backtothefuture.domain.bank.repository.BankRepository;
import com.backtothefuture.member.dto.response.BankListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankRepository bankRepository;

    public BankListResponseDto getAllBanks() {
        return BankListResponseDto.fromBanks(bankRepository.findAll());
    }
}
