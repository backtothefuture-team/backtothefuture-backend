package com.backtothefuture.domain.reservation;

import com.backtothefuture.domain.common.MutableBaseEntity;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.store.Store;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservation extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private Integer totalPrice; // 주문 총 금액

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reservationTime;

    public void increaseTotalPrice(int price) {
        this.totalPrice += price;
    }

}
