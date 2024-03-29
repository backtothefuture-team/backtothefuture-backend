package com.backtothefuture.event;

import com.backtothefuture.domain.common.util.RandomNumUtil;
import com.backtothefuture.event.dto.request.MailCertificateRequestDto;
import com.backtothefuture.event.dto.request.VerifyCertificateRequestDto;
import com.backtothefuture.event.service.CertificateService;
import com.backtothefuture.infra.config.BfTestConfig;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


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

        this.mockMvc.perform(post("/certificate/message/{phoneNumber}", phoneNumber)
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
    @DisplayName("인증 번호 검증 테스트")
    void verifyCertificateTest() throws Exception {
        //given
        String randomNum = RandomNumUtil.createRandomNum(6);
        Map<String, Object> verifyMap = new HashMap<>();
        verifyMap.put("phoneNumber", List.of("010", "1234", "5678"));
        verifyMap.put("certificationNumber", randomNum);

        VerifyCertificateRequestDto request = new VerifyCertificateRequestDto(List.of("010", "1234", "5678"), randomNum);

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

    @Test
    @DisplayName("인증 메일 전송 테스트")
    void sendCertificateMailTest() throws Exception {
        MailCertificateRequestDto requestDto = new MailCertificateRequestDto("test@example.com");
        when(certificateService.sendEmailCertificateNumber(any(MailCertificateRequestDto.class))).thenReturn(600);

        this.mockMvc.perform(post("/certificate/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("send-certificate-email",
                        resource(ResourceSnippetParameters.builder()
                                .description("인증 메일 전송 API 입니다.")
                                .tag("certificate")
                                .summary("인증 메일 전송 API")
                                .responseFields(
                                        fieldWithPath("email").type(SimpleType.STRING).description("이메일")
                                )
                                .responseSchema(Schema.schema("[request] send-certificate-email"))
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.mail_expiration_seconds").type(JsonFieldType.NUMBER).description("인증 만료 시간(초)")
                                )
                                .responseSchema(Schema.schema("[response] send-certificate-email")).build())));
    }

    @Test
    @DisplayName("인증 메일 검증 테스트")
    void verifyCertificateMailTest() throws Exception {
        this.mockMvc.perform(get("/certificate/email")
                        .param("email", "test@example.com")
                        .param("certificationNumber", "123456"))
                .andExpect(view().name("mail/verify-success"))
                .andDo(document("verify-certificate-email",
                        resource(ResourceSnippetParameters.builder()
                                .description("인증 메일 검증 API입니다. 메일 수신자가 링크를 클릭하고, 이 API를 통해 인증하게 됩니다.")
                                .tag("certificate")
                                .summary("인증 메일 검증")
                                .build())));
    }

    @Test
    @DisplayName("이메일 인증 상태 확인 테스트")
    void checkCertificateEmailStatusTest() throws Exception {
        when(certificateService.getCertificateEmailStatus("test@example.com")).thenReturn(true);

        this.mockMvc.perform(get("/certificate/email/{email}/status", "test@example.com"))
                .andExpect(status().isOk())
                .andDo(document("check-certificate-email-status",
                        resource(ResourceSnippetParameters.builder()
                                .description("이메일 인증 상태 확인 API입니다.")
                                .tag("certificate")
                                .summary("이메일 인증 상태 확인")
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.is_certificated").type(JsonFieldType.BOOLEAN).description("인증 여부. true: 인증 / false: 미인증")
                                )
                                .responseSchema(Schema.schema("[response] check-certificate-email-status"))
                                .build())));
    }
}
