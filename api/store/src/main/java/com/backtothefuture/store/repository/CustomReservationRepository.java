package com.backtothefuture.store.repository;


import com.backtothefuture.domain.reservation.Reservation;
import com.backtothefuture.store.dto.response.ReservationResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface CustomReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "select new com.backtothefuture.domain.reservation.dto.ReservationResponseDto(p.name,rp.quantity,p.price) "
            + "from Member m "
            + "inner join Reservation r on r.member.id = m.id "
            + "left join ReservationProduct rp on r.id = rp.reservation.id "
            + "left join Product p on rp.product.id = p.id "
            + "where m.id = :memberId and r.id = :reservationId", nativeQuery = true)
    List<ReservationResponseDto> getReservation(@Param("memberId") Long memberId, @Param("reservationId") Long reservationId);
}
