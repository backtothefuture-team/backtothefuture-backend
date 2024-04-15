package com.backtothefuture.store.controller;

import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.store.dto.response.ReservationResponseDto;
import com.backtothefuture.security.annotation.WithMockCustomUser;
import com.backtothefuture.store.dto.request.ReservationRequestDto;
import com.backtothefuture.store.dto.request.ReservationRequestItemDto;
import com.backtothefuture.store.service.ReservationService;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@ContextConfiguration
class ReservationControllerTest extends BfTestConfig {


    private MockMvc mockMvc;

    @MockBean
    private ReservationService mockReservationService;

    @Autowired
    private ObjectMapper objectMapper;

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
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(document("make-reservation",
                resource(ResourceSnippetParameters.builder()
                    .description("구매자 예약하기 API입니다.")
                    .tags("reservations")
                    .summary("구매자 예약 API")
                    .requestFields(
                        fieldWithPath("storeId").type(SimpleType.NUMBER).description("가게 ID 값입니다."),
                        fieldWithPath("orderRequestItems[].productId").type(SimpleType.NUMBER)
                            .description("상품 ID 값입니다."),
                        fieldWithPath("orderRequestItems[].quantity").type(SimpleType.NUMBER)
                            .description("주문한 수량 값입니다."),
                        fieldWithPath("reservationTime").type(SimpleType.STRING)
                            .description("예약 시간입니다. 'HH:mm' 형태입니다.")
                    )
                    .requestSchema(Schema.schema("[request] make-reservation"))
                    .responseFields(
                        fieldWithPath("code").type(SimpleType.NUMBER)
                            .description("HttpStatusCode 입니다."),
                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                        fieldWithPath("data.reservation_id").type(SimpleType.NUMBER)
                            .description("생성된 주문 ID 입니다.")
                    )
                    .responseSchema(Schema.schema("[response] make-reservation")).build()
                )));

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
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(document("get-reservation",
                resource(ResourceSnippetParameters.builder()
                    .description("구매자 예약 조회 API입니다.")
                    .tags("reservations")
                    .summary("구매자 예약 조회 API")
                    .pathParameters(
                        parameterWithName("reservationId").type(SimpleType.NUMBER)
                            .description("예약 ID")
                    )
                    .responseFields(
                        fieldWithPath("code").type(SimpleType.NUMBER)
                            .description("HttpStatusCode 입니다."),
                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                        fieldWithPath("data[].name").type(SimpleType.STRING)
                            .description("상품 이름입니다."),
                        fieldWithPath("data[].count").type(SimpleType.STRING)
                            .description("주문된 각 상품의 수량입니다."),
                        fieldWithPath("data[].price").type(SimpleType.STRING)
                            .description("주문된 각 상품의 금액입니다.")
                    )
                    .responseSchema(Schema.schema("[response] make-reservation")).build()
                )));
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
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(document("cancel-reservation",
                resource(ResourceSnippetParameters.builder()
                    .description("구매자 예약 취소 API입니다.")
                    .tags("reservations")
                    .summary("구매자 예약 취소 API")
                    .pathParameters(
                        parameterWithName("reservationId").type(SimpleType.NUMBER)
                            .description("예약 ID")
                    )
                    .responseFields(
                        fieldWithPath("code").type(SimpleType.NUMBER)
                            .description("HttpStatusCode 입니다."),
                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                        fieldWithPath("data").type(SimpleType.STRING).description("NO_CONTENT")
                    )
                    .responseSchema(Schema.schema("[response] cancel-reservation")).build()
                )));

    }
}