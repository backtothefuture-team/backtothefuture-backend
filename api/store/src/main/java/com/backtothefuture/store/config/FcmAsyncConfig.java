package com.backtothefuture.store.config;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * FCM 비동기 thread pool 설정 클래스입니다.
 */
@Configuration
public class FcmAsyncConfig implements AsyncConfigurer {

    private final Integer CORE_POOL_SIZE = 10;
    private final Integer QUEUE_CAPACITY = 10;
    private final Integer MAX_POOL_SIZE = 100;
    private final Integer KEEP_ALIVE_TIME = 30;
    private final String THREAD_NAME_PREFIX = "FCM-ASYNC-THREAD";

    @Bean("FCMAsyncBean")
    @Override
    public Executor getAsyncExecutor() {

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    // TODO: 비동기 과정에서 발생한 예외 처리
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }

}
