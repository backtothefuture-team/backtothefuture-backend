package com.backtothefuture.member.controller;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.domain.member.enums.RolesType;
import com.backtothefuture.global.RestAssuredTest;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.member.dto.response.KakaoAccount;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.service.MemberBusinessService;
import com.backtothefuture.member.service.MemberService;
import com.backtothefuture.member.service.OAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class MemberControllerTest extends RestAssuredTest {

    @Autowired
    private MemberService memberService;

    private MemberBusinessService memberBusinessService;

    @MockBean(name = "kakaoOAuthService")
    private OAuthService kakaoOAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    String accessToken;
    String refreshToken;

    @Test
    @DisplayName("1. 이메일 회원가입 테스트")
    void registerTest() throws Exception {
        MemberRegisterDto memberRegisterDto = MemberRegisterDto.builder()
                .email("user@email.com")
                .name("홍길동")
                .phoneNumber(List.of("010", "0000", "0000"))
                .password("123456")
                .passwordConfirm("123456")
                .accpetedTerms(List.of(1L, 2L, 3L, 4L, 5L))
                .build();

        RestAssured
                .given()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request", objectMapper.writeValueAsString(memberRegisterDto),
                        MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/v1/member/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("data.member_id", equalTo(1));
    }

    @Test
    @DisplayName("2. 일반 로그인 테스트")
    void loginTest() throws Exception {
        MemberRegisterDto memberRegisterDto = MemberRegisterDto.builder()
                .email("user@email.com")
                .name("홍길동")
                .phoneNumber(List.of("010", "0000", "0000"))
                .password("123456")
                .passwordConfirm("123456")
                .accpetedTerms(List.of(1L, 2L, 3L, 4L, 5L))
                .build();

        RestAssured
                .given()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("request", objectMapper.writeValueAsString(memberRegisterDto),
                        MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/v1/member/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("data.member_id", equalTo(1))
                .extract().path("data.accessToken");

        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("email", "user@email.com");
        loginMap.put("password", "123456");

        Response response = RestAssured
                .given()
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginMap)
                .when()
                .post("v1/member/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.accessToken", notNullValue())
                .body("data.refreshToken", notNullValue())
                .log().ifValidationFails()
                .extract().response();

        response.path("data.accessToken", accessToken);
        response.path("data.refreshToken", refreshToken);
    }

    @Test
    @DisplayName("3. oauth 회원 가입 테스트")
    void oauthLoginTest() throws Exception {
        // resource server에서 받아온 정보로 회원가입 진행
        OAuthLoginDto oauthLoginDto = new OAuthLoginDto("authorizationCode", ProviderType.KAKAO,
                RolesType.ROLE_USER, "state", "accessToken");
        KakaoAccount kakaoAccount = new KakaoAccount("이상민", "test@gmail.com", "010-0000-0000");
        // 로그인 성공 시 반환할 토큰 설정
        LoginTokenDto loginTokenDto = new LoginTokenDto("accessToken", "resfreshToken");
        // kakaoOAuthService의 login 메서드가 호출될 때 loginTokenDto를 반환하도록 설정
        when(kakaoOAuthService.getUserInfoFromResourceServer(any()))
                //then
                .thenReturn(loginTokenDto);

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(oauthLoginDto))
                .when()
                .post("/v1/member/login/oauth")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Disabled
    @DisplayName("4. 토큰 갱신 테스트")
    void refreshToken() throws Exception {

        RefreshTokenRequestDto oldToken = new RefreshTokenRequestDto(refreshToken);
        LoginTokenDto tokenDto = new LoginTokenDto(accessToken, refreshToken);

        when(memberService.refreshToken(anyString(), anyLong())).thenReturn(tokenDto);

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accessToken)
                .body(objectMapper.writeValueAsString(oldToken))
                .when()
                .post("/v1/member/refresh")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
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

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(businessInfoValidateRequestDto))
                .when()
                .post("/v1/member/business/info/validation")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사업자 번호 상태 검증 테스트")
    void businessNumberStatusTest() throws Exception {
        // 사업자 번호 상태 검증 요청 정보
        Map<String, String> requestbody = Map.of("businessNumber", "0000000000");

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(requestbody))
                .when()
                .post("/v1/member/business/number/status")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
