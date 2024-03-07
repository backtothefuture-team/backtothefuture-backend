package com.backtothefuture.security.config.audit;

import java.util.Optional;

public interface PrincipalProvider {
	Optional<String> getPrincipal();
}
