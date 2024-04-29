package com.backtothefuture.domain.term.repository;

import com.backtothefuture.domain.term.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {

}
