package com.example.restcontrollerdemo.config

import com.example.restcontrollerdemo.security.AuthenticatedUser
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.utils.SpringDocUtils
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
                    .description("Spring Boot 3 + Kotlin + WebFlux with @RestController and coroutines")
                    .version("1.0.0"),
            ).components(
                Components().addSecuritySchemes(
                    BEARER,
                    SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"),
                ),
            ).addSecurityItem(SecurityRequirement().addList(BEARER))

    private companion object {
        const val BEARER = "bearerAuth"

        init {
            // Resolved from the token, not from the request: not an API parameter.
            SpringDocUtils.getConfig().addRequestWrapperToIgnore(AuthenticatedUser::class.java)
        }
    }
}
