package com.backtothefuture.security.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backtothefuture.security.exception.CustomSecurityException;

/**
 * AuthenticationManager 셋팅
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final PasswordEncoder passwordEncoder;
	private final MemberUserDetailsService memberUserDetailsService;

	public CustomAuthenticationProvider(
		PasswordEncoder passwordEncoder,
		@Qualifier("memberUserDetailsService") MemberUserDetailsService memberUserDetailsService
	){
		this.passwordEncoder = passwordEncoder;
		this.memberUserDetailsService = memberUserDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String password = authentication.getCredentials().toString();

		UserDetailsImpl userDetails;

		userDetails = (UserDetailsImpl)memberUserDetailsService.loadUserByUsername(email);

		// OAuth 로그인 관련 추가예정

		// 비밀번호 확인
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new CustomSecurityException(CHECK_ID_OR_PASSWORD);
		}

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
