package com.backtothefuture.security.service;

import static com.backtothefuture.domain.common.enums.MemberErrorCode.*;
import static com.backtothefuture.domain.member.enums.StatusType.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backtothefuture.domain.member.Member;
import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.security.exception.CustomSecurityException;

import lombok.RequiredArgsConstructor;

@Service
@Qualifier("memberUserDetailsService")
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomSecurityException(NOT_FIND_MEMBER_EMAIL));

		if(member.getStatus().equals(INACTIVE)) {
			throw new CustomSecurityException(DELETE_MEMBER);
		}
		return UserDetailsImpl.from(member);
	}
}
