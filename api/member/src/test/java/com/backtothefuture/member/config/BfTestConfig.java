package com.backtothefuture.member.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BfTestConfig {

    private static final String DRIVER_CLASS_NAME = "org.testcontainers.jdbc.ContainerDatabaseDriver";
    private static final String URL = "jdbc:tc:mysql:8.0:///test";
    private static final String ROOT = "sa";
    private static final String ROOT_PASSWORD = "";
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:5.0.3-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getFirstMappedPort());
    }

    @Container
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.driver-class-name", () -> DRIVER_CLASS_NAME);
        dynamicPropertyRegistry.add("spring.datasource.url", () -> URL);
        dynamicPropertyRegistry.add("spring.datasource.username", () -> ROOT);
        dynamicPropertyRegistry.add("spring.datasource.password", () -> ROOT_PASSWORD);
        dynamicPropertyRegistry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }
}
