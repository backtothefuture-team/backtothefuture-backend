package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.MemberUpdateRequestDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.OAuthException;
import com.backtothefuture.member.service.MemberService;
import com.backtothefuture.member.service.OAuthService;
import com.backtothefuture.security.service.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


    @PostMapping("/login")
    public ResponseEntity<BfResponse<?>> login(@Valid @RequestBody MemberLoginDto memberLoginDto) {
        return ResponseEntity.ok(new BfResponse<>(memberService.login(memberLoginDto)));
    }

    @Operation(
            summary = "회원 가입",
            description = "회원 가입 API 입니다. 이미지는 'image/png', 'image/jpeg' 형식을 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "등록 성공",
                            content = @Content(schema = @Schema(implementation = BfResponse.class),
                                    examples = {
                                            @ExampleObject(name = "success", value = "{\"code\": 201, \"message\": \"정상적으로 생성되었습니다.\", \"data\": {\"member_id\": 1}}")
                                    }))
            })
    @SecurityRequirements(value = {}) // no security
    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BfResponse<?>> registerMember(
            @Parameter(description = "요청 정보입니다.")
            @Valid @RequestPart(value = "request") MemberRegisterDto reqMemberDto,
            @Parameter(description = "프로필 이미지로 사용할 이미지를 첨부해 주세요.")
            @RequestPart(value = "file", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BfResponse<>(CREATE,
                        Map.of("member_id", memberService.registerMember(reqMemberDto, thumbnail))));
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

    @PatchMapping("/{memberId}")
    public ResponseEntity<BfResponse<?>> updateMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "회원 ID입니다.")
            @PathVariable Long memberId,
            @Parameter(description = "회원 정보입니다.")
            @Valid @RequestBody MemberUpdateRequestDto memberUpdateDto
    ) {
        memberService.updateMemberInfo(userDetails, memberId, memberUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }
}
