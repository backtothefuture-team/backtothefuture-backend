package com.backtothefuture.domain.bank.repository;

import com.backtothefuture.domain.bank.Bank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByCode(String code);

    List<Bank> findAll();
}
