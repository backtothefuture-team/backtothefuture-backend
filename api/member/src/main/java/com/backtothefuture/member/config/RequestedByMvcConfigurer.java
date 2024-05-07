package com.backtothefuture.member.config;

import com.backtothefuture.infra.config.MultipartJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
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
    private final ObjectMapper objectMapper;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(requestedByInterceptor);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MultipartJackson2HttpMessageConverter(objectMapper));
    }
}
