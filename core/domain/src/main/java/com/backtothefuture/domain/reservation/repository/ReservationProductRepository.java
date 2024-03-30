package com.backtothefuture.domain.reservation.repository;

import com.backtothefuture.domain.reservation.ReservationProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationProductRepository extends JpaRepository<ReservationProduct, Long> {

    @Query("select r from ReservationProduct r where r.reservation.id = :reservationId")
    List<ReservationProduct> findAllByReservation(@Param("reservationId") Long reservationId);
}
