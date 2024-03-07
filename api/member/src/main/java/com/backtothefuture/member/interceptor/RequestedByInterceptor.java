package com.backtothefuture.member.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestedByInterceptor implements WebRequestInterceptor {
	public static final String REQUESTED_BY_HEADER = "requested-by";

	/**
	 * 전처리
	 */
	@Override
	public void preHandle(WebRequest request) throws Exception {
		String requestedBy = request.getHeader(REQUESTED_BY_HEADER);
	}

	/**
	 * 후처리
	 */
	@Override
	public void postHandle(WebRequest request, ModelMap model) throws Exception {

	}

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception {

	}

}
