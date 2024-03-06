package com.backtothefuture.security.config.audit;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.backtothefuture.security.service.UserDetailsImpl;

@Component
public class AuthenticationHolderImpl implements AuthenticationHolder, PrincipalProvider {
	private Authentication authentication;

	@Override
	public Optional<Authentication> getAuthentication() {
		return Optional.of(authentication);
	}

	@Override
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	@Override
	public Optional<String> getPrincipal() {
		Optional<UserDetailsImpl> userDetails = getAuthentication()
			.map(auth -> (UserDetailsImpl)auth.getPrincipal());
		return userDetails.map(UserDetailsImpl::getUsername);
	}
}

