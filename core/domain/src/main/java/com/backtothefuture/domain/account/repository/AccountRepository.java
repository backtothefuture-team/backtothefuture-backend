package com.backtothefuture.domain.account.repository;

import com.backtothefuture.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
