package com.backtothefuture.domain.reservation.repository;

import com.backtothefuture.domain.reservation.ReservationProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationProductRepository extends JpaRepository<ReservationProduct, Long> {
}
