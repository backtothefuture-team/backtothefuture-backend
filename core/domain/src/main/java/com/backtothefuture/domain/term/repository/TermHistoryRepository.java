package com.backtothefuture.domain.term.repository;

import com.backtothefuture.domain.term.TermHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermHistoryRepository extends JpaRepository<TermHistory, Long> {
}
