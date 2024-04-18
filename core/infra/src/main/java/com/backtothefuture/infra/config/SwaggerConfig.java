package com.backtothefuture.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0.0")
                .title("백투더퓨처 API Documentation")
                .description("백투더퓨처 API 문서입니다.");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME); // 헤더에 토큰 포함
        Components components = new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}
