package com.backtothefuture.member.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.backtothefuture.member.interceptor.RequestedByInterceptor;

import lombok.RequiredArgsConstructor;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class RequestedByMvcConfigurer implements WebMvcConfigurer {
	private final RequestedByInterceptor requestedByInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addWebRequestInterceptor(requestedByInterceptor);
	}
}
