package com.backtothefuture.domain.reservation.repository;


import com.backtothefuture.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

}
