package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_PRODUCT_ID;
import static com.backtothefuture.domain.common.enums.ReservationErrorCode.NOT_ENOUGH_STOCK_QUANTITY;
import static com.backtothefuture.domain.common.enums.ReservationErrorCode.NOT_FOUND_RESERVATION_PRODUCT;
import static com.backtothefuture.domain.common.enums.ReservationErrorCode.NOT_VALID_RESERVATION_TIME;
import static com.backtothefuture.domain.common.enums.StoreErrorCode.NOT_FOUND_STORE_ID;

import com.backtothefuture.domain.common.util.fcm.FCMUtil;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.reservation.Reservation;
import com.backtothefuture.domain.reservation.ReservationProduct;
import com.backtothefuture.domain.reservation.ReservationStatusHistory;
import com.backtothefuture.domain.reservation.enums.OrderType;
import com.backtothefuture.domain.reservation.repository.ReservationProductRepository;
import com.backtothefuture.domain.reservation.repository.ReservationRepository;
import com.backtothefuture.domain.reservation.repository.ReservationStatusHistoryRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.dto.response.ReservationResponseDto;
import com.backtothefuture.store.exception.MemberException;
import com.backtothefuture.store.exception.ProductException;
import com.backtothefuture.store.exception.ReservationException;
import com.backtothefuture.store.exception.StoreException;
import com.backtothefuture.store.repository.CustomReservationRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationProductRepository reservationProductRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final CustomReservationRepository customReservationRepository;
    private final ReservationStatusHistoryRepository reservationStatusHistoryRepository;
    private final FCMUtil fcmUtil;

    @Transactional
    public Long makeReservation(Long memberId, ReservationRequestDto dto) throws FirebaseMessagingException {

        Member member = memberRepository.findById(memberId)  // 현재 회원 조회
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        Store store = storeRepository.findById(dto.storeId())  // 주문하고자 하는 가게 조회
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        if (!validateReservationTime(dto.reservationTime(), store.getEndTime())) // 영업 종료 30분 이전인지 판단
        {
            throw new ReservationException(NOT_VALID_RESERVATION_TIME);
        }

        Reservation reservation = Reservation.builder()
                .store(store)
                .member(member)
                .reservationTime(dto.reservationTime())
                .build();
        reservationRepository.save(reservation);

        reservationStatusHistoryRepository.save(     // 주문 상태 이력 생성
                ReservationStatusHistory.builder()
                        .reservation(reservation)
                        .orderType(OrderType.REGISTRATION)
                        .eventTime(LocalDateTime.now()) // 예약 접수 기간 초기화 ( 예약 시간 x )
                        .build());

        updateStock(dto, reservation);  // 재고 차감
        fcmUtil.sendReservationRegisterMessage(store.getMember().getRegistrationToken()); // 가게 사장님에게 알림 전송
        return reservation.getId();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDto> getReservation(UserDetailsImpl userDetails,
                                                       Long reservationId) {
        //TODO: memberId 비교해서 권한 체크
        List<ReservationResponseDto> reservationResponseDto =
                customReservationRepository.getReservation(userDetails.getId(), reservationId);

        reservationResponseDto.forEach(ReservationResponseDto::calculateTotalPrice);

        return reservationResponseDto;
    }

    @Transactional
    public void cancelReservation(UserDetailsImpl userDetails, Long reservationId) {
        //TODO: memberId 비교해서 권한 체크
        List<ReservationProduct> reservationProducts = reservationProductRepository.findAllByReservation(
                reservationId);

        if (reservationProducts.size() == 0) {
            throw new ReservationException(NOT_FOUND_RESERVATION_PRODUCT);
        }

        reservationProducts.forEach((reservationProduct) -> {
            Product product = productRepository.findById(reservationProduct.getProduct().getId())
                    .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT_ID));
            product.updateStockWhenCancel(reservationProduct.getQuantity()); // 취소된 상품에 대해서 재고 증가
            reservationProductRepository.delete(reservationProduct); // 주문 상품 엔티티 삭제
        });

        reservationRepository.deleteById(reservationId);
    }

    /**
     * 1. 상품 조회 2. 재고, 주문 수량 비교 3. 재고 차감
     */
    private void updateStock(ReservationRequestDto dto, Reservation reservation) {

        dto.orderRequestItems().forEach(itemDto -> {

            Product product = productRepository.findProductWithPessimisticLockById(
                            itemDto.productId())  // 주문하고자 하는 상품 조회
                    .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT_ID));

            if (product.getStockQuantity() < itemDto.quantity())  // 재고가 부족하면 예외 처리
            {
                throw new ReservationException(NOT_ENOUGH_STOCK_QUANTITY);
            }

            int price = product.updateStockWhenReserve(
                    itemDto.quantity()); // 재고 차감하고 상품에 대한 주문 금액 반환
            reservation.increaseTotalPrice(price); // 주문 금액 반영

            reservationProductRepository.save(
                    ReservationProduct.builder()
                            .reservation(reservation)
                            .product(product)
                            .quantity(itemDto.quantity())
                            .build());
        });
    }

    private boolean validateReservationTime(LocalDateTime reservationTime, LocalTime endTime) {
        LocalTime time = LocalTime.of(reservationTime.getHour(), reservationTime.getMinute());
        return time.plusMinutes(30).isBefore(endTime); // 예약 시간이 영업 종료 30분 이전인지 판단
    }

}
