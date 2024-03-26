package com.backtothefuture.store.controller;

import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.service.StoreService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class StoreControllerTest extends BfTestConfig {
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
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
                .build();
        when(storeService.registerStore(storeRegisterDto)).thenReturn(1L);

        this.mockMvc.perform(post("/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRegisterDto))
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isCreated())
                .andDo(document("register-store",
                        resource(ResourceSnippetParameters.builder()
                                .description("가게 등록 API 입니다.")
                                .tags("stores")
                                .summary("가게 등록 API")
                                // request
                                .requestHeaders(
                                        headerWithName("Authorization").type(SimpleType.STRING).description("JWT 인증 토큰. 'Bearer ${Jwt Token}' 형식으로 입력해 주세요.")
                                )
                                .requestFields(
                                        fieldWithPath("name").type(SimpleType.STRING).description("가게 이름입니다."),
                                        fieldWithPath("description").type(SimpleType.STRING).description("가게 상세 설명입니다."),
                                        fieldWithPath("location").type(SimpleType.NUMBER).description("가게 위치입니다."),
                                        fieldWithPath("contact").type(JsonFieldType.ARRAY).optional().description("연락처입니다. 문자열 배열로 입력해주세요.").optional(),
                                        fieldWithPath("image").type(SimpleType.STRING).optional().description("썸네일 이미지 입니다.")
                                )
                                .requestSchema(Schema.schema("[request] store-register"))
                                // response
                                .responseFields(
                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                                        fieldWithPath("data.store_id").type(SimpleType.NUMBER).description("생성된 가게 ID 입니다.")
                                )
                                .responseSchema(Schema.schema("[response] store-register")).build()
                        )));
    }
}