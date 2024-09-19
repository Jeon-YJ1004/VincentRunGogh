package com.vincentrungogh.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    //파이썬과 rest api 통신할 때 사용하는 restTemplate 빈 설정
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
