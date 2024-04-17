package com.backtothefuture.domain.reservation.repository;

import com.backtothefuture.domain.reservation.ReservationStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationStatusHistoryRepository extends JpaRepository<ReservationStatusHistory, Long> {

    List<ReservationStatusHistory> findByReservationId(Long reservationId);
}
