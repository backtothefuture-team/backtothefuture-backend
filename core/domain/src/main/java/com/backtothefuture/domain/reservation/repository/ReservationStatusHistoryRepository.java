package com.backtothefuture.domain.reservation.repository;

import com.backtothefuture.domain.reservation.ReservationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationStatusHistoryRepository extends JpaRepository<ReservationStatusHistory, Long> {
}
