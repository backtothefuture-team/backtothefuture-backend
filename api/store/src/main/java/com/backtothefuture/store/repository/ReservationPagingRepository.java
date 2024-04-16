package com.backtothefuture.store.repository;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.reservation.Reservation;
import com.backtothefuture.domain.reservation.enums.OrderType;
import com.backtothefuture.store.dto.response.DoneReservationPagingResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationPagingRepository extends JpaRepository<Reservation, Long> {

    //TODO: 쿼리 하나 vs 여러 쿼리 나눠서 트랜잭션 성능 비교

    /**
     * 고객의 완료된 주문 내역 조회 ( 상품 정보 x )
     */
    @Query("SELECT new com.backtothefuture.store.dto.response.DoneReservationPagingResponseDto"
            + "(s.image,s.name,r.id,r.reservationTime,r.totalPrice) "
            + "FROM Member m "
            + "INNER JOIN Reservation r ON r.member.id = m.id "
            + "LEFT JOIN ReservationStatusHistory rsh ON rsh.reservation.id = r.id "
            + "LEFT JOIN Store s ON s.id = r.store.id "
            + "WHERE m.id = :memberId AND (:cursorId = 0L OR r.id < :cursorId) AND rsh.orderType IN :orderTypes "
            + "ORDER BY r.id DESC ")
    Slice<DoneReservationPagingResponseDto> getMemberReservations(@Param("memberId") Long memberId,
                                                                  @Param("cursorId") Long lastReservationId,
                                                                  @Param("orderTypes") List<OrderType> orderTypes,
                                                                  Pageable pageable);

    @Query("SELECT p "
            + "FROM ReservationProduct rp "
            + "INNER JOIN Product p ON rp.product.id = p.id "
            + "WHERE rp.reservation.id = :reservationId")
    List<Product> getProductsByReservationId(@Param("reservationId") Long reservationId);
}
