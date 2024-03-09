package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.*;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.member.enums.ProviderType;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.exception.OAuthException;
import com.backtothefuture.member.service.OAuthService;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	@Qualifier("kakaoOAuthService")
	private final OAuthService kakaoOAuthService;
	@Qualifier("naverOAuthService")
	private final OAuthService naverOAuthService;
	@PostMapping("/login")
	public ResponseEntity<BfResponse<?>> login(
		@Valid @RequestBody MemberLoginDto memberLoginDto) {
		return ResponseEntity.ok(new BfResponse<>(memberService.login(memberLoginDto)));
	}

	@PostMapping("/register")
	public ResponseEntity<BfResponse<?>> registerMember(
		@Valid @RequestBody MemberRegisterDto reqMemberDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new BfResponse<>(CREATE, Map.of("id", memberService.registerMember(reqMemberDto))));
	}

	@PostMapping("/login/oauth")
	public ResponseEntity<BfResponse<?>> oauthLogin(
		@Valid @RequestBody OAuthLoginDto OAuthLoginDto) {

		if(OAuthLoginDto.getProviderType() == ProviderType.KAKAO){ // 카카오 로그인
			LoginTokenDto loginTokenDto = kakaoOAuthService.getUserInfoFromResourceServer(OAuthLoginDto);
			return ResponseEntity.ok(new BfResponse<>(loginTokenDto));
		} else if (OAuthLoginDto.getProviderType() == ProviderType.NAVER) { // 네이버 로그인
			LoginTokenDto loginTokenDto = naverOAuthService.getUserInfoFromResourceServer(OAuthLoginDto);
			return ResponseEntity.ok(new BfResponse<>(loginTokenDto));
		} else { // 구글 로그인

		};
		throw new OAuthException(OAuthErrorCode.NOT_MATCH_OAUTH_TYPE);
	}
}
