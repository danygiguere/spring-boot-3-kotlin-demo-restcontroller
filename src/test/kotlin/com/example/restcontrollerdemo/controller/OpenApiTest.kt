package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.support.IntegrationTest
import org.junit.jupiter.api.Test

class OpenApiTest : IntegrationTest() {
    @Test
    fun `swagger ui redirects`() {
        webTestClient
            .get()
            .uri("/swagger-ui.html")
            .exchange()
            .expectStatus()
            .is3xxRedirection
    }

    @Test
    fun `api docs list every route with typed responses`() {
        webTestClient
            .get()
            .uri("/v3/api-docs")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.paths['/auth/dev-token'].post")
            .exists()
            .jsonPath("$.paths['/users'].get")
            .exists()
            .jsonPath("$.paths['/users/me/profile'].put")
            .exists()
            .jsonPath("$.paths['/users/{id}/liked-posts'].get")
            .exists()
            .jsonPath("$.paths['/users/{id}/images'].get")
            .exists()
            .jsonPath("$.paths['/posts'].post")
            .exists()
            .jsonPath("$.paths['/posts/{id}'].delete")
            .exists()
            .jsonPath("$.paths['/posts/{id}/likes'].put")
            .exists()
            // Inferred from the signature `suspend fun findById(...): PostDetails`, no annotation needed.
            .jsonPath("$.paths['/posts/{id}'].get.responses['200'].content['*/*'].schema['\$ref']")
            .isEqualTo("#/components/schemas/PostDetails")
            // Lists are cursor pages: one documented `cursor` parameter, no per-direction id parameters.
            .jsonPath("$.paths['/posts'].get.responses['200'].content['*/*'].schema['\$ref']")
            .isEqualTo("#/components/schemas/CursorPagePostSummary")
            .jsonPath("$.paths['/posts'].get.parameters[?(@.name == 'cursor')].description")
            .isNotEmpty
            .jsonPath("$.paths['/users'].get.parameters[?(@.name == 'cursor')]")
            .exists()
            .jsonPath("$.paths['/users'].get.parameters[?(@.name == 'afterId')]")
            .isEmpty
            .jsonPath("$.paths['/posts'].get.parameters[?(@.name == 'beforeId')]")
            .isEmpty
            .jsonPath("$.components.schemas.CursorPagePostSummary.properties.nextCursor")
            .exists()
            // AuthenticatedUser comes from the token: not documented as a parameter.
            .jsonPath("$.paths['/users/me'].get.parameters")
            .doesNotExist()
            .jsonPath("$.components.securitySchemes.bearerAuth")
            .exists()
    }
}
