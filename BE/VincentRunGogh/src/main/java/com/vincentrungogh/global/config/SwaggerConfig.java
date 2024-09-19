package com.vincentrungogh.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                //bearer token 설정하기
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                //문서 설명
                .info(new Info().title("Vincent Run Gogh API")
                        .description("Vincent Run Gogh API 문서")
                        .version("1.0"))
                //첫번째 url -> http 서버 연결하는 방식
                //두번째 url -> https 서버 연결하는 방식
                //세번째 url -> 로컬 서버 연결하는 방식
                .servers(Arrays.asList(new Server().url("http://j11b307.p.ssafy.io:8080/api/v1"), new Server().url("https://j11b307.p.ssafy.io/api/v1"), new Server().url("http://localhost:8080/api/v1")));  // 서버 URL을 HTTPS로 설정 및 기본 경로 추가
    }
}

