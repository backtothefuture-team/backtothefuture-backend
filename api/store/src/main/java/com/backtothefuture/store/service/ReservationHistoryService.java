package com.backtothefuture.store.service;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.reservation.enums.OrderType;
import com.backtothefuture.domain.reservation.repository.ReservationStatusHistoryRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.response.DoneReservationPagingResponseDto;
import com.backtothefuture.store.dto.response.MemberDoneReservationResponseDto;
import com.backtothefuture.store.repository.CustomReservationRepository;
import com.backtothefuture.store.repository.ReservationPagingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationHistoryService {


    private final ReservationPagingRepository reservationPagingRepository;
    private final List<OrderType> proceedingOrder = List.of(OrderType.REGISTRATION, OrderType.CONFIRMATION,
            OrderType.PICKUP_WAITING);
    private final List<OrderType> doneOrder = List.of(OrderType.PICKUP_DONE);
    private final String PRODUCT_NAME = "productName";

    @Transactional
    public List<MemberDoneReservationResponseDto> getMemberDoneReservations(UserDetailsImpl userDetails, Long cursorId,
                                                                            Integer size) {

        boolean isLast = true; // 추가 데이터가 존재하는지 조회
        List<MemberDoneReservationResponseDto> response = new ArrayList<>();

        // 픽업 완료된 과거 주문 내역 Pagination
        Slice<DoneReservationPagingResponseDto> memberProceedingReservations = reservationPagingRepository.getMemberReservations(
                1L, cursorId, doneOrder, Pageable.ofSize(size));

        if (memberProceedingReservations.hasNext()) {  // 다음 페이지가 존재하는지 판단
            isLast = false;
        }
        if (memberProceedingReservations.hasContent()) {// 반환된 데이터가 존재하면 실행

            memberProceedingReservations.getContent()
                    .forEach(dto -> {
                        response.add(MemberDoneReservationResponseDto.builder()
                                .storeImg(dto.storeImg())
                                .name(dto.name())
                                .reservationId(dto.reservationId())
                                .reservationTime(dto.reservationTime())
                                .totalPrice(dto.totalPrice())
                                .productNames(
                                        reservationPagingRepository.getProductsByReservationId(dto.reservationId())
                                                .stream()
                                                .map(Product::getName)
                                                .map(str -> Map.of(PRODUCT_NAME, str))
                                                .collect(Collectors.toList()))
                                .build());
                    });

        }
        return response;
    }
}
