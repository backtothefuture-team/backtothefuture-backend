package com.backtothefuture.store.service;

import static com.backtothefuture.domain.common.enums.StoreErrorCode.*;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backtothefuture.domain.member.repository.MemberRepository;
import com.backtothefuture.domain.store.Store;
import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.security.service.UserDetailsImpl;
import com.backtothefuture.store.dto.request.StoreRegisterDto;
import com.backtothefuture.store.exception.StoreException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
	private final MemberRepository memberRepository;
	private final StoreRepository storeRepository;

	/**
	 * 가게 등록
	 */
	@Transactional
	public Long registerStore(StoreRegisterDto storeRegisterDto) {
		Optional<UserDetails> userDetails = getUserDetails();

		return userDetails.flatMap(userDetail -> {
			if(userDetail instanceof UserDetailsImpl) {
				UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetail;
				return memberRepository.findById(userDetailsImpl.getId())
					.map(member -> {
						Store store = StoreRegisterDto.toEntity(storeRegisterDto, member);
						return storeRepository.save(store).getId();
					});
			}
			return Optional.empty();
		}).orElseThrow(() -> new StoreException(CHECK_MEMBER));
	}

	/**
	 * 인증된 사용자 정보 조회
	 */
	private Optional<UserDetails> getUserDetails() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.filter(Authentication::isAuthenticated)
			.map(Authentication::getPrincipal)
			.map(principal -> {
				if(principal instanceof UserDetails) {
					return ((UserDetails) principal);
				}else {
					return null;
				}
			});
	}
}
