package com.backtothefuture.security.config.audit;

import java.util.Optional;

import org.springframework.security.core.Authentication;

public interface AuthenticationHolder {
	Optional<Authentication> getAuthentication();
	void setAuthentication(Authentication authentication);
}
