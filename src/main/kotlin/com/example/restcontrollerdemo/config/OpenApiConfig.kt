package com.example.restcontrollerdemo.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("RestController Demo API")
                    .description("Demonstrates Spring Boot 3 + Kotlin + WebFlux with RestController")
                    .version("1.0.0"),
            )
}
