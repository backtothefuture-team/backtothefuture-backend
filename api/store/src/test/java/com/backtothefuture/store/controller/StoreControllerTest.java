package com.backtothefuture.store.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.service.StoreService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class StoreControllerTest extends BfTestConfig {

    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                // TODO: JWT 토큰 인증 처리 -> 실제 토큰을 발급받아 사용 or mocking(현재적용됨)
                //.apply(springSecurity()) // Spring Security 설정 적용
                .build();
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("가게 등록 테스트")
    void registerProductTest() throws Exception {
        // given
        Long storeId = 1L;
        StoreRegisterDto storeRegisterDto = StoreRegisterDto.builder()
                .name("가게 이름")
                .description("가게 설명")
                .location("가게 위치")
                .contact(List.of("010", "0000", "0000"))
                .image("이미지 url")
                .startTime(LocalTime.of(10, 00))
                .endTime(LocalTime.of(21, 00))
                .build();
        when(storeService.registerStore(storeRegisterDto)).thenReturn(1L);

        this.mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRegisterDto))
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isCreated());
    }
}
