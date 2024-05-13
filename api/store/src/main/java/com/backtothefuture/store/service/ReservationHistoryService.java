package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.ReservationErrorCode.NOT_FOUND_PROCEEDING_RESERVATION;

import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.reservation.enums.OrderType;
import com.backtothefuture.domain.reservation.repository.ReservationStatusHistoryRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.response.MemberDoneReservationResponseDto;
import com.backtothefuture.store.dto.response.MemberProgressReservationResponseDto;
import com.backtothefuture.store.dto.response.ProgressReservationHistoryResponseDto;
import com.backtothefuture.store.dto.response.ReservationListResponseDto;
import com.backtothefuture.store.dto.response.MemberDoneReservationListDto;
import com.backtothefuture.store.dto.response.ReservationProductNameResponseDto;
import com.backtothefuture.store.exception.ReservationException;
import com.backtothefuture.store.repository.ReservationPagingRepository;
import java.util.ArrayList;
import java.util.List;
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
    private final ReservationStatusHistoryRepository reservationStatusHistoryRepository;
    private final List<OrderType> proceedingOrder = List.of(
            OrderType.REGISTRATION, OrderType.CONFIRMATION, OrderType.PICKUP_WAITING);
    private final List<OrderType> doneOrder = List.of(OrderType.PICKUP_DONE);
    private final String PRODUCT_NAME = "productName";

    @Transactional(readOnly = true)
    public MemberDoneReservationResponseDto getMemberDoneReservations(UserDetailsImpl userDetails, Long cursorId,
                                                                      Integer size) {

        boolean isLast = true; // 추가 데이터가 존재하는지 조회
        List<MemberDoneReservationListDto> response = new ArrayList<>();

        // 픽업 완료된 과거 주문 내역 Pagination
        Slice<ReservationListResponseDto> memberProceedingReservations = reservationPagingRepository.getMemberReservations(
                userDetails.getId(), cursorId, doneOrder, Pageable.ofSize(size));

        if (memberProceedingReservations.hasNext()) {  // 다음 페이지가 존재하는지 판단
            isLast = false;
        }
        if (memberProceedingReservations.hasContent()) {// 반환된 데이터가 존재하면 실행

            memberProceedingReservations.getContent()
                    .forEach(dto -> {

                        List<ReservationProductNameResponseDto> productNames = reservationPagingRepository.getProductsByReservationId(
                                        dto.reservationId())
                                .stream()
                                .map(Product::getName)
                                .map(ReservationProductNameResponseDto::new)
                                .collect(Collectors.toList());

                        response.add(MemberDoneReservationListDto.builder()
                                .storeImg(dto.storeImg())
                                .name(dto.name())
                                .reservationId(dto.reservationId())
                                .reservationTime(dto.reservationTime())
                                .totalPrice(dto.totalPrice())
                                .productNames(productNames)
                                .build());
                    });

        }
        return new MemberDoneReservationResponseDto(response, isLast);
    }

    @Transactional(readOnly = true)
    public List<MemberProgressReservationResponseDto> getMemberProceedingReservation(UserDetailsImpl userDetails) {

        List<MemberProgressReservationResponseDto> response = new ArrayList<>();

        List<ReservationListResponseDto> memberReservations = reservationPagingRepository.getMemberReservations(
                userDetails.getId());

        if (memberReservations.isEmpty()) { // 진행 중인 주문이 존재하지 않을 경우,
            throw new ReservationException(NOT_FOUND_PROCEEDING_RESERVATION);
        }

        memberReservations
                .forEach(dto -> {

                    List<ReservationProductNameResponseDto> productNames = reservationPagingRepository.getProductsByReservationId(
                                    dto.reservationId())
                            .stream()
                            .map(Product::getName)
                            .map(ReservationProductNameResponseDto::new)
                            .collect(Collectors.toList());

                    List<ProgressReservationHistoryResponseDto> reservationHistories = reservationStatusHistoryRepository.findByReservationId(
                                    dto.reservationId())
                            .stream()
                            .map(entity ->
                                    new ProgressReservationHistoryResponseDto(entity.getOrderType(),
                                            entity.getEventTime()))
                            .collect(Collectors.toList());

                    response.add(MemberProgressReservationResponseDto.builder()
                            .storeImg(dto.storeImg())
                            .name(dto.name())
                            .reservationId(dto.reservationId())
                            .reservationTime(dto.reservationTime())
                            .totalPrice(dto.totalPrice())
                            .productNames(productNames)
                            .reservationHistories(reservationHistories)
                            .build());
                });

        return response;
    }

}
