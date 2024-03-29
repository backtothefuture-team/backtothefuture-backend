package com.backtothefuture.domain.reservation.repository;

import com.backtothefuture.domain.reservation.Reservation;

import com.backtothefuture.domain.reservation.dto.ReservationResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select new com.backtothefuture.domain.reservation.dto.ReservationResponseDto(p.name,rp.quantity,p.price) "
            + "from Member m "
            + "inner join Reservation r on r.member.id = m.id "
            + "left join ReservationProduct rp on r.id = rp.reservation.id "
            + "left join Product p on rp.product.id = p.id "
            + "where m.id = :memberId and r.id = :reservationId")
    List<ReservationResponseDto> getReservation(@Param("memberId") Long memberId, @Param("reservationId") Long reservationId);
}
