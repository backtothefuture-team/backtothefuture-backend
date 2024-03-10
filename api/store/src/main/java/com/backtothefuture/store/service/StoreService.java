package com.backtothefuture.store.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backtothefuture.domain.store.repository.StoreRepository;
import com.backtothefuture.store.dto.request.StoreRegisterDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
	private final StoreRepository storeRepository;

	/**
	 * 가게 등록
	 */
	@Transactional
	public Long registerStore(StoreRegisterDto storeRegisterDto) {
		Optional<String> userDetail = getUserInfo();

		System.out.println("username: " + userDetail);

		return 1L;
	}

	/**
	 * 인증된 사용자 정보 조회
	 */
	private Optional<String> getUserInfo() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.filter(Authentication::isAuthenticated)
			.map(Authentication::getPrincipal)
			.map(principal -> {
				if(principal instanceof UserDetails) {
					return ((UserDetails) principal).getUsername();
				}else {
					return principal.toString();
				}
			});
	}
}
