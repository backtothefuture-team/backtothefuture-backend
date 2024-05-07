package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.BusinessInfoValidateRequestDto;
import com.backtothefuture.member.dto.request.BusinessNumberValidateRequestDto;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.member.dto.request.RegistrationTokenRequestDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.OAuthException;
import com.backtothefuture.member.service.MemberBusinessService;
import com.backtothefuture.member.service.MemberService;
import com.backtothefuture.member.service.OAuthService;
import com.backtothefuture.security.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원 API", description = "회원 관련 API 입니다.")
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
            @Valid @RequestPart(value = "request") MemberRegisterDto reqMemberDto,
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE, Map.of("id", memberService.registerMember(reqMemberDto, thumbnail))));
    }

    @Operation(
            summary = "소셜 로그인",
            description = "소셜 로그인 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "소셜 로그인 성공 응답입니다.", useReturnTypeSchema = true)
            })
    @SecurityRequirements(value = {})
    @PostMapping("/login/oauth")
    public ResponseEntity<BfResponse<LoginTokenDto>> oauthLogin(@Valid @RequestBody OAuthLoginDto OAuthLoginDto) {
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
            default -> throw new

                    OAuthException(OAuthErrorCode.NOT_MATCH_OAUTH_TYPE);
        }

    }

    @Operation(
            summary = "토큰 갱신",
            description = "토큰 갱신 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 갱신에 성공했습니다.", useReturnTypeSchema = true)
            })
    @PostMapping("/refresh")
    public ResponseEntity<BfResponse<LoginTokenDto>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(new BfResponse<>(memberService.refreshToken(dto.refreshToken(), userDetails.getId())));
    }


    @PostMapping("/business/info/validation")
    @Operation(summary = "사업자 정보 검증", description = "입력된 사업자 정보의 유효성을 검증합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사업자 정보 검증 유효성 검사 결과", useReturnTypeSchema = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "valid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": true}}"),
                                    @ExampleObject(name = "invalid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": false}}")
                            }
                    )
            )
    })
    public ResponseEntity<BfResponse<?>> validateBusinessNumber(
            @Valid @RequestBody BusinessInfoValidateRequestDto businessNumberValidateRequestDto) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessInfo(businessNumberValidateRequestDto))));
    }

    @PostMapping("/business/number/status")
    @Operation(summary = "사업자 번호 상태 조회", description = "입력된 사업자 정보의 유효성을 검증합니다.")
    @SecurityRequirements(value = {}) // no security
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사업자 번호 검증 유효성 검사 결과", useReturnTypeSchema = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "valid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": true}}"),
                                    @ExampleObject(name = "invalid", value = "{\"code\": 200, \"message\": \"정상 처리되었습니다.\", \"data\": {\"isValid\": false}}")
                            }
                    )
            )
    })
    public ResponseEntity<BfResponse<?>> businessNumberStatus(
            @RequestBody BusinessNumberValidateRequestDto requestDto) {
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS,
                Map.of("isValid", memberBusinessService.validateBusinessNumber(requestDto.businessNumber()))));
    }

    @Operation(summary = "사용자 기기 등록", description = "FCM 사용자 기기 등록 토큰 저장 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 기기 등록 토큰 저장 성공",
                    content = @Content(schema = @Schema(implementation = BfResponse.class))
            )})
    @PostMapping("/registration/token")
    public ResponseEntity<BfResponse<Void>> saveRegistrationToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody RegistrationTokenRequestDto registrationTokenRequestDto
    ) {
        memberService.saveRegistrationToken(userDetails, registrationTokenRequestDto);
        return ResponseEntity.ok().body(new BfResponse<>(SUCCESS));
    }
}
