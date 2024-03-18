package com.backtothefuture.member.service;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.dto.response.NaverAccessTokenDto;
import com.backtothefuture.member.dto.response.NaverUserInfo;
import com.backtothefuture.member.exception.OAuthException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

// @Service("naverOAuthService")
@RequiredArgsConstructor
public class NaverOAuthService implements OAuthService {

    @Value("${oauth.naver.client_id}")
    private String clientId;
    @Value("${oauth.naver.client_secret}")
    private String clientSecret;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final String contentType = "application/x-www-form-urlencoded;charset=utf-8";

    @Override
    public String getAccessToken(OAuthLoginDto oAuthLoginDto) {

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("code", oAuthLoginDto.authorizationCode());
        requestBody.add("state", oAuthLoginDto.state());

        WebClient webclient = WebClient.builder()
            .baseUrl("https://nid.naver.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
            .build();

        NaverAccessTokenDto responseToken = webclient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/oauth2.0/token")
                .queryParams(requestBody)
                .build())
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(NaverAccessTokenDto.class);
                } else if (response.statusCode().is4xxClientError()) { // 4xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_WEBCLIENT_REQUEST_IN_USER_INFO);
                } else if (response.statusCode().is5xxServerError()) { // 5xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_OUTER_SYSTEM_ERROR);
                } else {
                    throw new RuntimeException("webclient error"); // 그 외 에러 handle
                }
            }).block();

        return responseToken.accessToken();
    }

    @Override
    @Transactional
    public LoginTokenDto getUserInfoFromResourceServer(OAuthLoginDto OAuthLoginDto) {

        String token = getAccessToken(OAuthLoginDto); // naver server에서 access token 발급

        WebClient webclient = WebClient.builder()
            .baseUrl("https://openapi.naver.com/v1/nid/me")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();

        NaverUserInfo userInfo = webclient.get()
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(NaverUserInfo.class);
                } else {
                    throw new WebClientResponseException(500, "NAVER_INTERNAL_SERVER_ERROR", null,
                        null, null);
                }
            }).block();

        Optional<Member> member = isMember(userInfo.naverResponse().name(),
            userInfo.naverResponse().phoneNumber());

        if (member.isPresent()) {// 기존회원은 바로 로그인 처리
            return memberService.OAuthLogin(member.get());
        }

        // 비회원임으로 회원가입 처리 후 로그인 처리
        // 회원 가입
        // random password 생성
        String password = UUID.randomUUID().toString().replace("-", "");
        Member newMember = userInfo.toEntity(OAuthLoginDto, password);
        newMember.setPassword(passwordEncoder.encode(password));
        memberRepository.save(newMember);
        // 로그인
        MemberLoginDto memberLoginDto = ConvertUtil.toDtoOrEntity(newMember,
            MemberLoginDto.class);
        memberLoginDto.setPassword(password);
        return memberService.login(memberLoginDto);

    }

    @Override
    public Optional<Member> isMember(String name, String phoneNumber) {

        return memberRepository.findByNameAndPhoneNumber(name, phoneNumber);

    }
}
