package com.backtothefuture.event.config;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoolsmsConfig {

    @Value("${spring.certification.coolsms.api}")
    private String coolsmsApiKey;

    @Value("${spring.certification.coolsms.secret}")
    private String coolsmsApiSecretKey;

    private final String coolsmsDomain = "https://api.coolsms.co.kr";

    @Bean
    public DefaultMessageService messageService() {
        return NurigoApp.INSTANCE.initialize(coolsmsApiKey, coolsmsApiSecretKey, coolsmsDomain);
    }

}
