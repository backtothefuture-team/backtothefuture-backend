package com.backtothefuture.member.service;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.KakaoAccessTokenDto;
import com.backtothefuture.member.dto.response.KakaoUserInfo;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;


@Service("kakaoOAuthService")
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    @Value("${oauth.kakao.client.id}")
    private String clientId;

    @Value("${oauth.kakao.redirect.url}")
    private String redirectUrl;

    @Value("${oauth.kakao.client.secret}")
    private String clientSecret;

    private final String contentType = "application/x-www-form-urlencoded;charset=utf-8";

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    @Override
    public String getAccessToken(OAuthLoginDto OAuthLoginDto) {  // token 받아옴

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", clientId);
        requestBody.add("redirect_uri", redirectUrl);
        requestBody.add("code", OAuthLoginDto.getAuthorizationCode());
        requestBody.add("client_secret", clientSecret);

        WebClient webclient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, contentType)
            .build();

        //access token 요청
        KakaoAccessTokenDto responseToken = webclient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/oauth/token")
                .queryParams(requestBody)
                .build())
            .exchangeToMono(response -> {
                if (response.statusCode().equals(HttpStatus.OK)) {
                    return response.bodyToMono(KakaoAccessTokenDto.class);
                } else if (response.statusCode().is4xxClientError()) { // 4xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_WEBCLIENT_REQUEST);
                } else if (response.statusCode().is5xxServerError()) { // 5xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_OUTER_SYSTEM_ERROR);
                } else {
                    throw new RuntimeException("webclient error"); // 그 외 에러 handle
                }
            }).block(); // 동기 처리
        return responseToken.getAccessToken();
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
                    throw new OAuthException(OAuthErrorCode.BAD_WEBCLIENT_REQUEST);
                } else if (response.statusCode().is5xxServerError()) { // 5xx 에러 handle
                    throw new OAuthException(OAuthErrorCode.BAD_OUTER_SYSTEM_ERROR);
                } else {
                    throw new RuntimeException("webclient error"); // 그 외 에러 handle
                }
            }).block();

        Member member = isMember(userInfo.getAuthId());

        if(member == null){ // 비회원임으로 회원가입 처리
            Member newMember = userInfo.toEntity(OAuthLoginDto);
            memberRepository.save(newMember);
            MemberLoginDto memberLoginDto = ConvertUtil.toDtoOrEntity(newMember, MemberLoginDto.class);
            return memberService.login(memberLoginDto);
        } else { // 기존회원은 바로 로그인 처리
            MemberLoginDto memberLoginDto = ConvertUtil.toDtoOrEntity(member, MemberLoginDto.class);
            return memberService.login(memberLoginDto);
        }
    }

    @Override
    public Member isMember(Long authId) {

        return memberRepository.findByAuthId(String.valueOf(authId)).orElse(null);

    }
}
