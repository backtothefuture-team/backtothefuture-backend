package com.backtothefuture.domain.residence.repository;

import com.backtothefuture.domain.residence.Residence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {
}
