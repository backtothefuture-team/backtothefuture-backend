package com.backtothefuture.infra.config;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.cdimascio.dotenv.Dotenv;


@Testcontainers
public abstract class BfTestConfig {

    private static final int REDIS_PORT = 6379;

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8")
            .withUsername("root")
            .withPassword("1234")
            .withDatabaseName("test")
            .withInitScript("init.sql");

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:5.0.3-alpine")
            .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT));
    }

    // .env 환경변수 등록
    @DynamicPropertySource
    static void dotenvProperties(DynamicPropertyRegistry registry) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./../../")
                .load();
        dotenv.entries().forEach(entry -> {
            registry.add(entry.getKey(), entry::getValue);
        });
    }
}
