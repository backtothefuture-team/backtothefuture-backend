package com.backtothefuture.member.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backtothefuture.domain.common.repository.RedisRepository;
import com.backtothefuture.domain.common.util.ConvertUtil;
import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.member.dto.request.MemberLoginDto;
import com.backtothefuture.member.dto.request.MemberRegisterDto;
import com.backtothefuture.member.dto.response.LoginTokenDto;
import com.backtothefuture.member.exception.MemberException;
import com.backtothefuture.security.jwt.JwtProvider;
import com.backtothefuture.security.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RedisRepository redisRepository;

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

		UserDetailsImpl userDetail = (UserDetailsImpl)authenticated.getPrincipal();

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
	public Long registerMember(MemberRegisterDto memberRegisterDto) {
		// 아이디 중복 체크
		if(memberRepository.existsByEmail(memberRegisterDto.getEmail())){
			throw new MemberException(DUPLICATED_MEMBER_EMAIL);
		}else if(memberRepository.existsByPhoneNumber(memberRegisterDto.getPhoneNumber())){
			throw new MemberException(DUPLICATED_MEMBER_PHONE_NUMBER);
		}

		// 비밀번호 확인
		validatePassword(memberRegisterDto.getPassword(), memberRegisterDto.getPasswordConfirm());

		Member member = ConvertUtil.toDtoOrEntity(memberRegisterDto, Member.class);
		member.setPassword(passwordEncoder.encode(memberRegisterDto.getPassword()));
		member.setPhoneNumber(memberRegisterDto.getPhoneNumber());

		return memberRepository.save(member).getId();
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

	@Transactional
	public LoginTokenDto refreshToken(String oldRefreshToken, Long memberId){

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(NOT_FIND_MEMBER_ID));

		// redis 갱신된 refresh token 유효성 검증
		if(!redisRepository.hasKey(member.getId()))
			throw new MemberException(NOT_FIND_REFRESH_TOKEN);

		// redis에 저장된 토큰과 비교
		if(!redisRepository.getRefreshToken(member.getId()).get("refreshToken").equals(oldRefreshToken))
			throw new MemberException(NOT_MATCH_REFRESH_TOKEN);

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
}
