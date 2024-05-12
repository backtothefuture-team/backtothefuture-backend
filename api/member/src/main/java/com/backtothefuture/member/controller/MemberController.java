package com.backtothefuture.member.controller;

import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.CREATE;
import static com.backtothefuture.domain.common.enums.GlobalSuccessCode.SUCCESS;

import com.backtothefuture.domain.common.enums.OAuthErrorCode;
import com.backtothefuture.domain.response.BfResponse;
import com.backtothefuture.domain.response.ErrorResponse;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberPasswordResetRequestDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.MemberUpdateRequestDto;
import com.backtothefuture.member.dto.request.OAuthLoginDto;
import com.backtothefuture.member.dto.request.RefreshTokenRequestDto;
import com.backtothefuture.member.dto.request.RegistrationTokenRequestDto;
import com.backtothefuture.member.dto.request.TermHistoryUpdateDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.dto.response.MemberInfoDto;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(
            summary = "일반 로그인",
            description = "일반 로그인 API입니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공 응답입니다.", useReturnTypeSchema = true)
            })
    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public ResponseEntity<BfResponse<LoginTokenDto>> login(
            @Valid @RequestBody MemberLoginDto memberLoginDto
    ) {
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

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 정보 조회", description = "회원의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true)
    public ResponseEntity<BfResponse<MemberInfoDto>> getMemberInfo(
            @Parameter(description = "회원 ID입니다.")
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(new BfResponse<>(memberService.getMemberInfo(memberId)));
    }

    @PatchMapping("/{memberId}")
    @Operation(summary = "회원 정보 업데이트", description = "회원의 정보를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 업데이트되었습니다.", content = @Content(schema = @Schema(implementation = BfResponse.class)))
    public ResponseEntity<BfResponse<?>> updateMemberInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "회원 ID입니다.")
            @PathVariable Long memberId,
            @Parameter(description = "업데이트 할 회원 정보입니다. 업데이트가 필요한 정보만 전달해주세요.")
            @Valid @RequestBody MemberUpdateRequestDto memberUpdateDto
    ) {
        memberService.updateMemberInfo(userDetails, memberId, memberUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    @PutMapping("/{memberId}/terms/{termId}")
    @Operation(
            summary = "약관 동의 여부 업데이트",
            description = "약관 동의 여부를 업데이트 합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true),
                    @Parameter(name = "termId", description = "약관 ID", required = true)
            }
    )
    @ApiResponse(responseCode = "200", description = "약관 동의 여부 업데이트 성공", content = @Content(schema = @Schema(implementation = BfResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "필수 약관에 동의하지 않았을 경우",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            name = "필수 약관 미동의 에러",
                            summary = "필수 약관 미동의",
                            description = "사용자가 필수 약관에 동의하지 않았을 때 반환되는 에러 메시지입니다.",
                            value = "{\"errorCode\": 400, \"errorMessage\": \"필수 약관에 동의해주세요.\"}"
                    )
            )
    )
    public ResponseEntity<BfResponse<?>> updateTermAgreement(
            @PathVariable Long memberId,
            @PathVariable Long termId,
            @RequestBody TermHistoryUpdateDto termHistoryUpdateDto
    ) {
        memberService.updateTermAgreement(memberId, termId, termHistoryUpdateDto);
        return ResponseEntity.ok(new BfResponse<>(SUCCESS));
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "회원 탈퇴", responses = {
            @ApiResponse(description = "탈퇴 성공", responseCode = "204", content = @Content(schema = @Schema(hidden = true)))},
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true)
            })
    public ResponseEntity<BfResponse<?>> deleteMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("memberId") Long memberId
    ) {
        memberService.inactiveMember(userDetails, memberId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{memberId}/password")
    @Operation(summary = "비밀번호 재설정", description = "회원 비밀번호 재설정 API 입니다.", responses = {
            @ApiResponse(description = "비밀번호 변경 성공", responseCode = "200", content = @Content(schema = @Schema(implementation = BfResponse.class)))},
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true)
            })
    public ResponseEntity<BfResponse<?>> resetPassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable("memberId") Long memberId,
            @Valid @RequestBody MemberPasswordResetRequestDto resetRequestDto
    ) {
        memberService.resetPassword(userDetails, memberId, resetRequestDto);
        return ResponseEntity.noContent().build();
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
