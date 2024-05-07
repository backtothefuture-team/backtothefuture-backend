package com.backtothefuture.member.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_EMAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.DUPLICATED_MEMBER_PHONE_NUMBER;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.IMAGE_UPLOAD_FAIL;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_FOUND_REFRESH_TOKEN;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.NOT_MATCH_REFRESH_TOKEN;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.PASSWORD_NOT_MATCHED;
import static com.backtothefuture.domain.common.enums.MemberErrorCode.UNSUPPORTED_IMAGE_EXTENSION;
import com.backtothefuture.domain.common.repository.RedisRepository;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.common.util.s3.S3Util;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.request.RegistrationTokenRequestDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.MemberException;
import com.backtothefuture.security.jwt.JwtProvider;
import com.backtothefuture.security.service.UserDetailsImpl;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RedisRepository redisRepository;
    private final S3Util s3Util;

    /**
     * 로그인, OAuth 신규 회원 로그인
     */
    public LoginTokenDto login(MemberLoginDto memberloginDto) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberloginDto.getEmail(),
                memberloginDto.getPassword()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetail = (UserDetailsImpl) authenticated.getPrincipal();

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = new LoginTokenDto(accessToken, refreshToken);

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    /**
     * 회원 등록
     */
    @Transactional
    public Long registerMember(MemberRegisterDto memberRegisterDto, MultipartFile thumbnail) {
        // 아이디 중복 체크
        if (memberRepository.existsByEmail(memberRegisterDto.getEmail())) {
            throw new MemberException(DUPLICATED_MEMBER_EMAIL);
        } else if (memberRepository.existsByPhoneNumber(memberRegisterDto.getPhoneNumber())) {
            throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
        }

        // 비밀번호 확인
        validatePassword(memberRegisterDto.getPassword(), memberRegisterDto.getPasswordConfirm());

        Member member = ConvertUtil.toDtoOrEntity(memberRegisterDto, Member.class);
        member.setPassword(passwordEncoder.encode(memberRegisterDto.getPassword()));
        member.setPhoneNumber(memberRegisterDto.getPhoneNumber());

        Long id = memberRepository.save(member).getId();

        // 이미지 업로드
        try {
            String imageUrl = s3Util.uploadMemberProfile(String.valueOf(id), thumbnail);
            member.setProfileUrl(imageUrl);
        } catch (IllegalArgumentException e) {
            throw new MemberException(UNSUPPORTED_IMAGE_EXTENSION);
        } catch (IOException e) {
            throw new MemberException(IMAGE_UPLOAD_FAIL);
        }

        return id;
    }

    /**
     * password, passwordConfirm 체크
     */
    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MemberException(PASSWORD_NOT_MATCHED);
        }
    }

    /**
     * OAuth 기존 회원 로그인
     */
    @Transactional
    public LoginTokenDto OAuthLogin(Member member) {

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String refreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), refreshToken);

        return loginTokenDto;
    }

    /**
     * 기존 refresh token으로 신규 access token, refresh token 발급
     */
    @Transactional
    public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // redis 갱신된 refresh token 유효성 검증
        if (!redisRepository.hasKey(member.getId())) {
            throw new MemberException(NOT_FOUND_REFRESH_TOKEN);
        }

        // redis에 저장된 토큰과 비교
        if (!redisRepository.getRefreshToken(member.getId()).get("refreshToken").equals(oldRefreshToken)) {
            throw new MemberException(NOT_MATCH_REFRESH_TOKEN);
        }

        UserDetailsImpl userDetail = (UserDetailsImpl) UserDetailsImpl.from(member);

        // accessToken, refreshToken 생성
        String accessToken = jwtProvider.createAccessToken(userDetail);
        String newRefreshToken = jwtProvider.createRfreshToken(userDetail);

        LoginTokenDto loginTokenDto = LoginTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();

        // redis 토큰 정보 저장
        redisRepository.saveToken(userDetail.getId(), newRefreshToken);

        return loginTokenDto;

    }

    /**
     * 사용자 기기 등록 토큰 저장
     */
    @Transactional
    public void saveRegistrationToken(UserDetailsImpl userDetail, RegistrationTokenRequestDto dto) {
        Member member = memberRepository.findById(userDetail.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));
        member.setRegistrationToken(dto.registrationToken());
    }
}
