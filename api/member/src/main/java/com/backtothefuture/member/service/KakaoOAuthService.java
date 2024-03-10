package com.backtothefuture.member.service;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.KakaoAccessToken;
import com.backtothefuture.member.dto.response.KakaoUserInfo;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.OAuthException;
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


@Service("kakaoOAuthService")
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    @Value("${oauth.kakao.client_id}")
    private String clientId;

    @Value("${oauth.kakao.redirect_url}")
    private String redirectUrl;

    @Value("${oauth.kakao.client_secret}")
    private String clientSecret;

    private final String contentType = "application/x-www-form-urlencoded;charset=utf-8";

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String getAccessToken(OAuthLoginDto OAuthLoginDto) {  // token 받아옴

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUrl);
        requestBody.add("code", OAuthLoginDto.authorizationCode());
        requestBody.add("client_secret", clientSecret);

        WebClient webclient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
            .build();

        //access token 요청
        KakaoAccessToken responseToken = webclient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/oauth/token")
                .queryParams(requestBody)
                .build())
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(KakaoAccessToken.class);
                } else if (response.statusCode().is4xxClientError()) { // 4xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_WEBCLIENT_REQUEST_IN_ACCESS_TOEKN);
                } else if (response.statusCode().is5xxServerError()) { // 5xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_OUTER_SYSTEM_ERROR);
                } else {
                    throw new RuntimeException("webclient error"); // 그 외 에러 handle
                }
            }).block(); // 동기 처리
        return responseToken.accessToken();
    }

    @Override
    @Transactional
    public LoginTokenDto getUserInfoFromResourceServer(OAuthLoginDto OAuthLoginDto) {  // 사용자 정보 받아옴

        String token = getAccessToken(OAuthLoginDto);

        WebClient webclient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com/v2/user/me")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();

        //kakao resource server로 사용자 정보 요청
        KakaoUserInfo userInfo = webclient.post()
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(KakaoUserInfo.class);
                } else if (response.statusCode().is4xxClientError()) { // 4xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_WEBCLIENT_REQUEST_IN_USER_INFO);
                } else if (response.statusCode().is5xxServerError()) { // 5xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_OUTER_SYSTEM_ERROR);
                } else {
                    throw new RuntimeException("webclient error"); // 그 외 에러 handle
                }
            }).block();

        Member member = isMember(String.valueOf(userInfo.authId()));

        if(member == null){ // 비회원임으로 회원가입 처리 후 로그인 처리
            // 회원 가입
            // random password 생성
            String password = UUID.randomUUID().toString().replace("-", "");
            Member newMember = userInfo.toEntity(OAuthLoginDto, password);
            newMember.setPassword(passwordEncoder.encode(password));
            memberRepository.save(newMember);
            // 로그인
            MemberLoginDto memberLoginDto = ConvertUtil.toDtoOrEntity(newMember, MemberLoginDto.class);
            memberLoginDto.setPassword(password);
            return memberService.login(memberLoginDto);
        } else { // 기존회원은 바로 로그인 처리
            return memberService.OAuthLogin(member);
        }
    }

    @Override
    public Member isMember(String authId) {

        return memberRepository.findByAuthId(authId).orElse(null);

    }
}
