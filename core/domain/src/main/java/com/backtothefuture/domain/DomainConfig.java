package com.backtothefuture.domain;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan("com.backtothefuture.domain")
@EnableJpaRepositories("com.backtothefuture.domain")
@EnableRedisRepositories("com.backtothefuture.domain")
public class DomainConfig {
}
