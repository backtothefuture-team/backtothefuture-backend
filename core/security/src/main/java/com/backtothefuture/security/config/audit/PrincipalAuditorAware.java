package com.backtothefuture.security.config.audit;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrincipalAuditorAware implements AuditorAware<String>, DateTimeProvider {
	private final ApplicationContext applicationContext;

	@Override
	public Optional<TemporalAccessor> getNow() {
		return Optional.of(ZonedDateTime.now());
	}

	@Override
	public Optional<String> getCurrentAuditor() {
		try{
			return Optional.of(applicationContext.getBean(PrincipalProvider.class))
				.flatMap(PrincipalProvider::getPrincipal);
		}catch(Exception e) {
			return Optional.of("SYSTEM");
		}
	}
}