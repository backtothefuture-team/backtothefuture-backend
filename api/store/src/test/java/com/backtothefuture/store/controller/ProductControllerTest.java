package com.backtothefuture.store.controller;

import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.service.ProductService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class ProductControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

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
    @DisplayName("상품 등록 테스트")
    void registerProductTest() throws Exception {
        // given
        Long storeId = 1L;
        ProductRegisterDto productRegisterDto = ProductRegisterDto.builder()
                .name("상품 이름")
                .description("상품 설명")
                .price(10000)
                .stockQuantity(1)
                .thumbnail("이미지 링크")
                .build();
        when(productService.registerProduct(storeId, productRegisterDto)).thenReturn(1L);

        this.mockMvc.perform(post("/products/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRegisterDto))
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isCreated())
                .andDo(document("register-product",
                        resource(ResourceSnippetParameters.builder()
                                .description("상품 등록 API 입니다.")
                                .tags("products")
                                .summary("상품 등록 API")
                                // request
                                .requestHeaders(
                                        headerWithName("Authorization").type(SimpleType.STRING).description("JWT 인증 토큰. 'Bearer ${Jwt Token}' 형식으로 입력해 주세요.")
                                )
                                .pathParameters(
                                        parameterWithName("storeId").type(SimpleType.NUMBER).description("가게 ID")
                                )
                                .requestFields(
                                        fieldWithPath("name").type(SimpleType.STRING).description("상품 이름입니다."),
                                        fieldWithPath("description").type(SimpleType.STRING).description("상품 상세 설명입니다."),
                                        fieldWithPath("price").type(SimpleType.NUMBER).description("상품 가격입니다. 0 이상의 수를 입력해주세요."),
                                        fieldWithPath("stockQuantity").type(SimpleType.NUMBER).description("상품 재고입니다. 0 이상의 수를 입력해주세요. 기본값은 0입니다.").optional(),
                                        fieldWithPath("thumbnail").type(SimpleType.STRING).description("썸네일 이미지 입니다.").optional()
                                )
                                .requestSchema(Schema.schema("[request] product-register"))
                                // response
                                .responseFields(
                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                                        fieldWithPath("data.product_id").type(SimpleType.NUMBER).description("생성된 상품 ID 입니다.")
                                )
                                .responseSchema(Schema.schema("[response] product-register")).build()
                        )));
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() throws Exception {
        this.mockMvc.perform(delete("/products/{storeId}/{productId}", 1, 1)
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isNoContent())
                .andDo(document("delete-product",
                        resource(ResourceSnippetParameters.builder()
                                .description("상품 삭제 API 입니다.")
                                .tags("products")
                                .summary("상품 삭제 API")
                                // request
                                .requestHeaders(
                                        headerWithName("Authorization").type(SimpleType.STRING).description("JWT 인증 토큰. 'Bearer ${Jwt Token}' 형식으로 입력해 주세요.")
                                )
                                .pathParameters(
                                        parameterWithName("storeId").type(SimpleType.NUMBER).description("가게 ID"),
                                        parameterWithName("productId").type(SimpleType.NUMBER).description("상품 ID")
                                )
                                .requestSchema(Schema.schema("[request] product-delete"))
                                // no response
                                .responseSchema(Schema.schema("[response] product-delete")).build()
                        )));
    }
}