package com.backtothefuture.store.controller;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backtothefuture.domain.common.util.s3.S3AsyncUtil;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.infra.config.S3Config;
import com.backtothefuture.store.dto.response.ProductResponseDto;
import com.backtothefuture.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class ProductControllerTest extends BfTestConfig {
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

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
        ProductResponseDto productResponseDto = new ProductResponseDto(productId, "상품1", "상품1 설명", 10000, 10,
                "thumbnail1");
        when(productService.getProduct(storeId, productId)).thenReturn(productResponseDto);

        // when & then
        this.mockMvc.perform(get("/stores/{storeId}/products/{productId}", storeId, productId)
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 상품 조회 테스트")
    void getAllProductsTest() throws Exception {
        // given
        List<ProductResponseDto> products = List.of(
                new ProductResponseDto(1L, "상품1", "상품1 설명", 10000, 10, "thumbnail1"),
                new ProductResponseDto(2L, "상품2", "상품2 설명", 20000, 20, "thumbnail2"));
        when(productService.getAllProducts()).thenReturn(products);

        // when & then
        this.mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser("USER")
//    @DisplayName("상품 등록 테스트")
//    void registerProductTest() throws Exception {
//        // given
//        Long storeId = 1L;
//        ProductRegisterDto productRegisterDto = ProductRegisterDto.builder()
//                .name("상품 이름")
//                .description("상품 설명")
//                .price(10000)
//                .stockQuantity(1)
//                .thumbnail("이미지 링크")
//                .build();
//        when(productService.registerProduct(storeId, productRegisterDto)).thenReturn(1L);
//
//        this.mockMvc.perform(post("/stores/{storeId}/products", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(productRegisterDto))
//                        .header("Authorization", "Bearer ${JWT Token}"))
//                .andExpect(status().isCreated())
//                .andDo(document("register-product",
//                        resource(ResourceSnippetParameters.builder()
//                                .description("상품 등록 API 입니다.")
//                                .tags("products")
//                                .summary("상품 등록 API")
//                                // request
//                                .requestHeaders(
//                                        headerWithName("Authorization").type(SimpleType.STRING).description("JWT 인증 토큰. 'Bearer ${Jwt Token}' 형식으로 입력해 주세요.")
//                                )
//                                .pathParameters(
//                                        parameterWithName("storeId").type(SimpleType.NUMBER).description("가게 ID")
//                                )
//                                .requestFields(
//                                        fieldWithPath("name").type(SimpleType.STRING).description("상품 이름입니다."),
//                                        fieldWithPath("description").type(SimpleType.STRING).description("상품 상세 설명입니다."),
//                                        fieldWithPath("price").type(SimpleType.NUMBER).description("상품 가격입니다. 0 이상의 수를 입력해주세요."),
//                                        fieldWithPath("stockQuantity").type(SimpleType.NUMBER).optional().description("상품 재고입니다. 0 이상의 수를 입력해주세요. 기본값은 0입니다.").optional(),
//                                        fieldWithPath("thumbnail").type(SimpleType.STRING).optional().description("썸네일 이미지 입니다.")
//                                )
//                                .requestSchema(Schema.schema("[request] product-register"))
//                                // response
//                                .responseFields(
//                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
//                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
//                                        fieldWithPath("data.product_id").type(SimpleType.NUMBER).description("생성된 상품 ID 입니다.")
//                                )
//                                .responseSchema(Schema.schema("[response] product-register")).build()
//                        )));
//    }

//    @Test
//    @WithMockUser("USER")
//    @DisplayName("상품 업데이트 테스트")
//    void updateProductTest() throws Exception {
//        // given
//        Long storeId = 1L;
//        Long productId = 1L;
//
//        ProductUpdateDto productUpdateDto = ProductUpdateDto.builder()
//                .name("업데이트된 상품 이름")
//                .description("업데이트된 상품 설명")
//                .price(15000)
//                .stockQuantity(5)
//                .thumbnail("업데이트된 이미지 링크")
//                .build();
//
//        ProductResponseDto productResponseDto = ProductResponseDto.builder()
//                .id(productId)
//                .name(productUpdateDto.name())
//                .description(productUpdateDto.description())
//                .price(productUpdateDto.price())
//                .stockQuantity(productUpdateDto.stockQuantity())
//                .thumbnail(productUpdateDto.thumbnail())
//                .build();
//
//        when(productService.partialUpdateProduct(eq(storeId), eq(productId), any(ProductUpdateDto.class))).thenReturn(productResponseDto);
//
//        // when & then
//        this.mockMvc.perform(patch("/stores/{storeId}/products/{productId}", storeId, productId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(productUpdateDto))
//                        .header("Authorization", "Bearer ${JWT Token}"))
//                .andExpect(status().isOk())
//                .andDo(document("update-product",
//                        resource(ResourceSnippetParameters.builder()
//                                .description("상품 업데이트 API 입니다. 업데이트 할 항목만 보내주세요.")
//                                .tags("products")
//                                .summary("상품 업데이트 API")
//                                // request
//                                .pathParameters(
//                                        parameterWithName("storeId").type(SimpleType.NUMBER).description("가게 ID"),
//                                        parameterWithName("productId").type(SimpleType.NUMBER).description("상품 ID")
//                                )
//                                .requestFields(
//                                        fieldWithPath("name").type(SimpleType.STRING).optional().description("상품 이름입니다."),
//                                        fieldWithPath("description").type(SimpleType.STRING).optional().description("상품 상세 설명입니다."),
//                                        fieldWithPath("price").type(SimpleType.NUMBER).optional().description("상품 가격입니다. 0 이상의 수를 입력해주세요."),
//                                        fieldWithPath("stockQuantity").type(SimpleType.NUMBER).optional().description("상품 재고입니다. 0 이상의 수를 입력해주세요."),
//                                        fieldWithPath("thumbnail").type(SimpleType.STRING).optional().description("썸네일 이미지 입니다.")
//                                )
//                                .requestSchema(Schema.schema("[reqyest] update-product"))
//                                // response
//                                .responseFields(
//                                        fieldWithPath("code").type(SimpleType.NUMBER).description("HttpStatusCode 입니다."),
//                                        fieldWithPath("message").type(SimpleType.STRING).description("응답 메시지 입니다."),
//                                        fieldWithPath("data.product.id").type(SimpleType.NUMBER).description("상품 ID"),
//                                        fieldWithPath("data.product.name").type(SimpleType.STRING).description("상품 이름"),
//                                        fieldWithPath("data.product.description").type(SimpleType.STRING).description("상품 설명"),
//                                        fieldWithPath("data.product.price").type(SimpleType.NUMBER).description("상품 가격"),
//                                        fieldWithPath("data.product.stockQuantity").type(SimpleType.NUMBER).description("재고 수량"),
//                                        fieldWithPath("data.product.thumbnail").type(SimpleType.STRING).description("썸네일 이미지 URL")
//                                )
//                                .responseSchema(Schema.schema("[response] update-product")).build()
//                        )));
//    }

    @Test
    @WithMockUser("USER")
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() throws Exception {
        this.mockMvc.perform(delete("/stores/{storeId}/products/{productId}", 1, 1)
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isNoContent());
    }
}
