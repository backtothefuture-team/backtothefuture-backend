package com.backtothefuture.store.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.backtothefuture.domain.common.util.s3.S3AsyncUtil;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.infra.config.S3Config;
import com.backtothefuture.security.annotation.WithMockCustomUser;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.dto.request.ReservationRequestItemDto;
import com.backtothefuture.store.dto.response.ReservationResponseDto;
import com.backtothefuture.store.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@ContextConfiguration
class ReservationControllerTest extends BfTestConfig {


    private MockMvc mockMvc;

    @MockBean
    private ReservationService mockReservationService;

    @Autowired
    private ObjectMapper objectMapper;

    // 임시 s3 관련 mockbean 설정..
    @MockBean
    private S3Util s3Util;

    @MockBean
    private S3Config s3Config;

    @MockBean
    private S3AsyncUtil s3AsyncUtil;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("상품 예약하기")
    @WithMockCustomUser
    void makeReservation() throws Exception {

        Long storeId = 1L;
        Long product1Id = 1L;
        Long product2Id = 2L;

        ReservationRequestDto reservationRequestDto = ReservationRequestDto.builder()
                .storeId(storeId)
                .orderRequestItems(List.of(new ReservationRequestItemDto(product1Id, 1),
                        new ReservationRequestItemDto(product2Id, 1)))
                .reservationTime(LocalTime.of(12, 00))
                .build();

        // TODO: 아래 코드가 테스트의 효과가 있는지 궁금합니다!
        when(mockReservationService.makeReservation(anyLong(),
                eq(reservationRequestDto))).thenReturn(
                1L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDto))
                        .header("Authorization", "Bearer ${JWT Token}")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("예약 조회하기")
    @WithMockCustomUser
    void getReservation() throws Exception {

        Long reservationId = 1L;

        List<ReservationResponseDto> reservationResponseDto = List.of(
                new ReservationResponseDto("product1", 1, 1000),
                new ReservationResponseDto("product2", 2, 2000)
        );

        when(mockReservationService.getReservation(any(), eq(reservationId))).thenReturn(
                reservationResponseDto);

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/reservations/{reservationId}", reservationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reservationResponseDto))
                                .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("구매자 예약 취소하기")
    @WithMockCustomUser
    void cancelReservation() throws Exception {

        Long reservationId = 1L;
        doNothing().when(mockReservationService).cancelReservation(any(), eq(reservationId));

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/reservations/{reservationId}", reservationId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
