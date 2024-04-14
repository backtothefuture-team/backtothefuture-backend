package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;

import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.OAuthException;
import com.backtothefuture.member.service.MemberBusinessService;
import com.backtothefuture.member.service.MemberService;
import com.backtothefuture.member.service.OAuthService;
import com.backtothefuture.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    @Qualifier("kakaoOAuthService")
    private final OAuthService kakaoOAuthService;
    @Qualifier("naverOAuthService")
    private final OAuthService naverOAuthService;

    private final MemberBusinessService memberBusinessService;

    @PostMapping("/login")
    public ResponseEntity<BfResponse<?>> login(@Valid @RequestBody MemberLoginDto memberLoginDto) {
        return ResponseEntity.ok(new BfResponse<>(memberService.login(memberLoginDto)));
    }

    @PostMapping("/register")

    public ResponseEntity<BfResponse<?>> registerMember(
            @Valid @RequestBody MemberRegisterDto reqMemberDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("id", memberService.registerMember(reqMemberDto))));
    }

    @PostMapping("/login/oauth")
    public ResponseEntity<BfResponse<?>> oauthLogin(@Valid @RequestBody OAuthLoginDto OAuthLoginDto) {

        switch (OAuthLoginDto.providerType()) {
            case KAKAO -> {
                LoginTokenDto loginTokenDto = kakaoOAuthService.getUserInfoFromResourceServer(OAuthLoginDto);
                return ResponseEntity.ok(new BfResponse<>(loginTokenDto));
            }
            case NAVER -> {
                LoginTokenDto loginTokenDto = naverOAuthService.getUserInfoFromResourceServer(OAuthLoginDto);
                return ResponseEntity.ok(new BfResponse<>(loginTokenDto));
            }
	 		/* google 소셜 로그인 추가 시 사용
	 		case GOOGLE -> {
	 		} */

	 		default -> throw new OAuthException(OAuthErrorCode.NOT_MATCH_OAUTH_TYPE);
	 	}
      
	 }

    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<?>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(memberService.refreshToken(dto.refreshToken(), userDetails.getId())));
    }


    @PostMapping("/business/validate-info")
    public ResponseEntity<BfResponse<?>> validateBusinessNumber(
            @Valid @RequestBody BusinessInfoValidateRequestDto businessNumberValidateRequestDto) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessInfo(businessNumberValidateRequestDto))));
    }

    @PostMapping("/business/validate-status")
    public ResponseEntity<BfResponse<?>> businessNumberStatus(@RequestBody Map<String, String> requestbody) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessNumber(requestbody.get("businessNumber")))));
    }

}
