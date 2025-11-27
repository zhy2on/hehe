package com.example.jwt.common.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	
	@Bean
	public OpenAPI openApi() {
		
		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("Bearer")
				.bearerFormat("JWT")
				.in(SecurityScheme.In.HEADER)
				.name("Authorization");
		
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("Json Web Token");
		
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("Json Web Token", securityScheme))
				.security(Collections.singletonList(securityRequirement))
				.info(new Info()
						.title("인증 / 인가 / 사용자 API")
						.description("인증, 인가, 사용자 전용 API")
						.version("v1.0"));
		
	}
}
