package com.backtothefuture.infra.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BfTestConfig {
	@Container
	private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8")
		.withUsername("test_user")
		.withPassword("1234")
		.withDatabaseName("");

	@Container
	static GenericContainer<?> redisContainer = new GenericContainer<>("redis:5.0.3-alpine")
		.withExposedPorts(6379);

	@DynamicPropertySource
	public static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mySQLContainer::getUsername);
		registry.add("spring.datasource.password", mySQLContainer::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", redisContainer::getHost);
		registry.add("spring.redis.port", () -> redisContainer.getFirstMappedPort());
	}
}
