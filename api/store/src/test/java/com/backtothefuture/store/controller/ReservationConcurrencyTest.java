package com.backtothefuture.store.controller;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.domain.member.enums.StatusType;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.product.Product;
import com.backtothefuture.domain.product.repository.ProductRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.dto.request.ReservationRequestItemDto;
import com.backtothefuture.store.exception.ProductException;
import com.backtothefuture.store.service.ReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.backtothefuture.domain.common.enums.ProductErrorCode.NOT_FOUND_PRODUCT_ID;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("구매자 예약 동시성 테스트")
    void ReservationConcurrencyTest() throws InterruptedException {
        //given
        Member customer1 = Member.builder() // 고객1 생성
                .name("이상민")
                .authId(null)
                .email("leesangmin@naver.com")
                .status(StatusType.ACTIVE)
                .provider(null)
                .roles(RolesType.ROLE_USER)
                .build();
        memberRepository.save(customer1);

        Member owner = Member.builder()  // 가게 주인 생성
                .name("owner")
                .authId(null)
                .email("email3@naver.com")
                .status(StatusType.ACTIVE)
                .provider(null)
                .roles(RolesType.ROLE_STORE_OWNER)
                .build();
        memberRepository.save(owner);

        Store store = Store.builder()  // 가게 생성
                .name("gs25")
                .description("편의점입니다.")
                .image("이미지 url")
                .contact("010-0000-0000")
                .location("서울")
                .member(owner)
                .build();
        storeRepository.save(store);

        Product product = Product.builder()
                .name("삼각김밥")
                .description("삼각김밥입니다.")
                .price(1000)
                .stockQuantity(1000)
                .thumbnail("nail test")
                .store(store)
                .build();
        productRepository.save(product);

        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
                .storeId(store.getId())
                .orderRequestItems(List.of(new ReservationRequestItemDto(product.getId(), 6)))
                .build();

        //when
        int threadCount = 200; // thread pool 속 thread 갯수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(200);
        AtomicInteger failureCount = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();
        /*
        주문 하나에 재고가 6개 차감된다. 재고는 1000로 초기화되어 있다.
        스레드 200개가 존재하고 200번의 주문 시도가 존재한다.
        166 * 6 = 996임으로
        성공 횟수는 166이고 실패 횟수는 34이다. 마지막 주문이 1000-996 = 4임으로 마지막 주문이 실패하고 남은 재고는 4이다.
         */
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 예약 실시
                    Long reservationId = reservationService.makeReservation(customer1.getId(), reservationRequestDto);
                    // 성공 횟수 증가
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    // 실패 횟수 증가
                    failureCount.getAndIncrement();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        productRepository.flush();
        //then
        Product findProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT_ID));
        Assertions.assertEquals(4, findProduct.getStockQuantity());
        Assertions.assertEquals(34, failureCount.get());
        Assertions.assertEquals(166, successCount.get());
    }
}
