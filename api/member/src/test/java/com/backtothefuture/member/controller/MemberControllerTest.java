package com.backtothefuture.member.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backtothefuture.domain.common.util.s3.S3AsyncUtil;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.infra.config.BfTestConfig;
import com.backtothefuture.infra.config.S3Config;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.member.dto.response.KakaoAccount;
import com.backtothefuture.member.dto.response.KakaoUserInfo;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.service.MemberBusinessService;
import com.backtothefuture.member.service.MemberService;
import com.backtothefuture.member.service.OAuthService;
import com.backtothefuture.security.annotation.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class MemberControllerTest extends BfTestConfig {

    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean(name = "kakaoOAuthService")
    private OAuthService kakaoOAuthService;

    @MockBean
    private MemberBusinessService memberBusinessService;

    @Autowired
    private ObjectMapper objectMapper;

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
                .build();
    }

//    @Test
//    @DisplayName("회원가입 테스트")
//    void registerTest() throws Exception {
//        // 회원가입 request 정보
//        Map<String, Object> memberMap = new HashMap<>();
//        memberMap.put("email", "kildong.hong@naver.com");
//        memberMap.put("name", "홍길동");
//        memberMap.put("password", "123456");
//        memberMap.put("passwordConfirm", "123456");
//        memberMap.put("phoneNumber", List.of("010", "1234", "5678"));
//
//        // 회원가입 성공 시, 반환할 값 (id) 설정
//        when(memberService.registerMember(any())).thenReturn(1L);
//
//        this.mockMvc.perform(post("/member/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberMap)))
//                .andExpect(status().isCreated())
//                .andDo(document("register-member",
//                        resource(ResourceSnippetParameters.builder()
//                                .description("회원가입 API 입니다.")
//                                .tags("member")
//                                .summary("회원가입 API")
//                                // request
//                                .requestFields(
//                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
//                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
//                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
//                                        fieldWithPath("passwordConfirm").type(JsonFieldType.STRING).description("비밀번호 확인"),
//                                        fieldWithPath("phoneNumber").type(JsonFieldType.ARRAY).description("전화번호")
//                                )
//                                .requestSchema(Schema.schema("[request] member-register"))
//                                // response
//                                .responseFields(
//                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
//                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
//                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID")
//                                )
//                                .responseSchema(Schema.schema("[response] member-register")).build()
//                        )));
//    }

    @Test
    @DisplayName("일반 로그인 테스트")
    void loginTest() throws Exception {
        // 로그인 요청에 필요한 정보를 HashMap으로 생성
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("email", "kildong.hong@naver.com");
        loginMap.put("password", "123456");

        // 로그인 성공 시 반환할 토큰 설정
        LoginTokenDto loginTokenDto = new LoginTokenDto("access token", "resfresh token");

        // memberService의 login 메서드가 호출될 때 loginTokenDto를 반환하도록 설정
        when(memberService.login(any())).thenReturn(loginTokenDto);

        // POST 요청으로 로그인 엔드포인트에 요청하고 응답 상태코드가 200인지 확인
        this.mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMap)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("oauth 회원 가입 테스트")
    void oauthLoginTest() throws Exception {
        // resource server에서 받아온 정보로 회원가입 진행
        // given
        OAuthLoginDto oauthLoginDto = new OAuthLoginDto("authorizationCode", ProviderType.KAKAO,
                RolesType.ROLE_USER, "state", "accessToken");
        KakaoAccount kakaoAccount = new KakaoAccount("이상민", "test@gmail.com", "010-0000-0000");
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(1L, kakaoAccount);
        // 로그인 성공 시 반환할 토큰 설정
        LoginTokenDto loginTokenDto = new LoginTokenDto("accessToken", "resfreshToken");
        // when
        // kakaoOAuthService의 login 메서드가 호출될 때 loginTokenDto를 반환하도록 설정
        when(kakaoOAuthService.getUserInfoFromResourceServer(any()))
                //then
                .thenReturn(loginTokenDto);

        this.mockMvc.perform(post("/member/login/oauth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oauthLoginDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 갱신 테스트")
    @WithMockCustomUser
    void refreshToken() throws Exception {

        RefreshTokenRequestDto oldToken = new RefreshTokenRequestDto("5324fdswqe13dew");
        LoginTokenDto tokenDto = new LoginTokenDto("access token", "resfresh token");

        when(memberService.refreshToken(anyString(), anyLong())).thenReturn(tokenDto);

        this.mockMvc.perform(post("/member/refresh").header("Authorization", "Bearer ${JWT Token}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldToken)))
                .andExpect(status().isOk());
    }

    @DisplayName("사업자 정보 유효성 검증 테스트")
    void validateBusinessInfoTest() throws Exception {
        // 사업자 정보 유효성 검사 요청 정보
        BusinessInfoValidateRequestDto businessInfoValidateRequestDto = new BusinessInfoValidateRequestDto(
                "0000000000", // 사업자번호
                "20000101", // 개업일자
                "홍길동", // 대표자 성명
                "홍길순", // 대표자 성명2 (선택사항)
                "(주)테스트", // 상호
                "0000000000000", // 법인등록번호 (선택사항)
                "", // 주업태명
                "", // 주종목명
                "" // 사업장주소
        );

        // 사업자 정보 검증 성공 시, 반환할 값 설정
        when(memberBusinessService.validateBusinessInfo(any())).thenReturn(true);

        this.mockMvc.perform(post("/member/business/validate-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(businessInfoValidateRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사업자 번호 상태 검증 테스트")
    void businessNumberStatusTest() throws Exception {
        // 사업자 번호 상태 검증 요청 정보
        Map<String, String> requestbody = Map.of("businessNumber", "0000000000");

        // 사업자 번호 상태 검증 성공 시, 반환할 값 설정
        when(memberBusinessService.validateBusinessNumber(anyString())).thenReturn(true);

        this.mockMvc.perform(post("/member/business/validate-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestbody)))
                .andExpect(status().isOk());
    }
}
