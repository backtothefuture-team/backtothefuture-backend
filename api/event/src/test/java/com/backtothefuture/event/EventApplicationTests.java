package com.backtothefuture.event;

import com.backtothefuture.domain.common.util.RandomNumUtil;
import com.backtothefuture.event.dto.request.VerifyCertificateRequest;
import com.backtothefuture.event.service.CertificateService;
import com.backtothefuture.infra.config.BfTestConfig;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;


@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@Slf4j
class EventApplicationTests extends BfTestConfig {

    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    @MockBean
    private RandomNumUtil randomNumUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
    @Test
    @DisplayName("인증 번호 발급 테스트")
    void getCertificateTest() throws Exception {
        //given
        String phoneNumber = "010-0000-0000";
        String randomNum = RandomNumUtil.createRandomNum(6);
        //when,then
        when(certificateService.getCertificateNumber(phoneNumber)).thenReturn(randomNum);

        this.mockMvc.perform(get("/certificate/message/{phoneNumber}", phoneNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("get-certificate-number",
                        resource(ResourceSnippetParameters.builder()
                                .description("인증 번호 발급 API입니다.")
                                .tags("certificate")
                                .summary("인증 번호 발급 API")
                                .requestFields()
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.certification_number").type(JsonFieldType.STRING).description("인증 번호")
                                )
                                .responseSchema(Schema.schema("[response] get-certificate-number")).build()
                        )));
    }

    @Test
    @DisplayName("인증 번호 발급 테스트")
    void verifyCertificateTest() throws Exception {
        //given
        String randomNum = RandomNumUtil.createRandomNum(6);
        Map<String, Object> verifyMap = new HashMap<>();
        verifyMap.put("phoneNumber", List.of("010", "1234", "5678"));
        verifyMap.put("certificationNumber", randomNum);

        VerifyCertificateRequest request = new VerifyCertificateRequest( List.of("010", "1234", "5678"), randomNum);

        //when,then
        this.mockMvc.perform(post("/certificate/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyMap)))
                .andExpect(status().isOk())
                .andDo(document("verify-certificate-number",
                        resource(ResourceSnippetParameters.builder()
                                .description("인증 번호 검증 API입니다.")
                                .tags("certificate")
                                .summary("인증 번호 검증 API")
                                .requestFields(
                                        fieldWithPath("phoneNumber").type(JsonFieldType.ARRAY).description("전화번호"),
                                        fieldWithPath("certificationNumber").type(JsonFieldType.STRING).description("인증번호")
                                )
                                .requestSchema(Schema.schema("[request] verify-certificate-number"))
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                )
                                .responseSchema(Schema.schema("[response] verify-certificate-number")).build()
                        )));
    }

}
