package com.backtothefuture.store.controller;

import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.dto.response.ProductResponseDto;
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

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    @DisplayName("상품 단건 조회 테스트")
    void getProductTest() throws Exception {
        // given
        Long storeId = 1L;
        Long productId = 1L;
        ProductResponseDto productResponseDto = new ProductResponseDto(productId, "상품1", "상품1 설명", 10000, 10, "thumbnail1");
        when(productService.getProduct(storeId, productId)).thenReturn(productResponseDto);

        // when & then
        this.mockMvc.perform(get("/store/{storeId}/products/{productId}", storeId, productId)
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isOk())
                .andDo(document("get-product-by-store",
                        resource(ResourceSnippetParameters.builder()
                                .description("상품 단건 조회 API입니다.")
                                .tags("products")
                                .summary("상품 단건 조회 API")
                                .pathParameters(
                                        parameterWithName("storeId").type(SimpleType.NUMBER).description("가게 ID"),
                                        parameterWithName("productId").type(SimpleType.NUMBER).description("상품 ID")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                                        fieldWithPath("data.product.id").type(SimpleType.NUMBER).description("상품 ID"),
                                        fieldWithPath("data.product.name").type(SimpleType.STRING).description("상품 이름"),
                                        fieldWithPath("data.product.description").type(SimpleType.STRING).description("상품 설명"),
                                        fieldWithPath("data.product.price").type(SimpleType.NUMBER).description("상품 가격"),
                                        fieldWithPath("data.product.stockQuantity").type(SimpleType.NUMBER).description("재고 수량"),
                                        fieldWithPath("data.product.thumbnail").type(SimpleType.STRING).description("썸네일 이미지 URL")
                                )
                                .responseSchema(Schema.schema("[response] get-product")).build()
                        )));
    }

    @Test
    @DisplayName("모든 상품 조회 테스트")
    void getAllProductsTest() throws Exception {
        // given
        List<ProductResponseDto> products = List.of(new ProductResponseDto(1L, "상품1", "상품1 설명", 10000, 10, "thumbnail1"),
                new ProductResponseDto(2L, "상품2", "상품2 설명", 20000, 20, "thumbnail2"));
        when(productService.getAllProducts()).thenReturn(products);

        // when & then
        this.mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isOk())
                .andDo(document("get-all-products",
                        resource(ResourceSnippetParameters.builder()
                                .description("모든 상품 조회 API입니다.")
                                .tags("products")
                                .summary("모든 상품 조회 API")
                                // response
                                .responseFields(
                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
                                        fieldWithPath("data.products[].id").type(SimpleType.NUMBER).description("상품 ID"),
                                        fieldWithPath("data.products[].name").type(SimpleType.STRING).description("상품 이름"),
                                        fieldWithPath("data.products[].description").type(SimpleType.STRING).description("상품 설명"),
                                        fieldWithPath("data.products[].price").type(SimpleType.NUMBER).description("상품 가격"),
                                        fieldWithPath("data.products[].stockQuantity").type(SimpleType.NUMBER).description("재고 수량"),
                                        fieldWithPath("data.products[].thumbnail").type(SimpleType.STRING).description("썸네일 이미지 URL")
                                )
                                .responseSchema(Schema.schema("[response] get-all-products")).build()
                        )));
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

        this.mockMvc.perform(post("/store/{storeId}/products", 1)
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
        this.mockMvc.perform(delete("/store/{storeId}/products/{productId}", 1, 1)
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