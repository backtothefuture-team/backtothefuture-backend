package com.backtothefuture.store.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.store.dto.request.ProductRegisterDto;
import com.backtothefuture.store.dto.request.ProductUpdateDto;
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
import org.springframework.http.MediaType;
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

        this.mockMvc.perform(post("/stores/{storeId}/products", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRegisterDto))
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("상품 업데이트 테스트")
    void updateProductTest() throws Exception {
        // given
        Long storeId = 1L;
        Long productId = 1L;

        ProductUpdateDto productUpdateDto = ProductUpdateDto.builder()
                .name("업데이트된 상품 이름")
                .description("업데이트된 상품 설명")
                .price(15000)
                .stockQuantity(5)
                .thumbnail("업데이트된 이미지 링크")
                .build();

        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .id(productId)
                .name(productUpdateDto.name())
                .description(productUpdateDto.description())
                .price(productUpdateDto.price())
                .stockQuantity(productUpdateDto.stockQuantity())
                .thumbnail(productUpdateDto.thumbnail())
                .build();

        when(productService.partialUpdateProduct(eq(storeId), eq(productId), any(ProductUpdateDto.class))).thenReturn(
                productResponseDto);

        // when & then
        this.mockMvc.perform(patch("/stores/{storeId}/products/{productId}", storeId, productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDto))
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("상품 삭제 테스트")
    void deleteProductTest() throws Exception {
        this.mockMvc.perform(delete("/stores/{storeId}/products/{productId}", 1, 1)
                        .header("Authorization", "Bearer ${JWT Token}"))
                .andExpect(status().isNoContent());
    }
}
