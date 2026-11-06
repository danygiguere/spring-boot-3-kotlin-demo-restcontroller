package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.dto.UserRequest
import com.example.restcontrollerdemo.shared.config.SharedTestContainers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest : SharedTestContainers() {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `create user - success returns 201 with user data`() {
        webTestClient
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(UserRequest(name = "Jane Smith", email = "jane.smith@example.com", phoneNumber = "+1-555-987-6543"))
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.id")
            .isNumber
            .jsonPath("$.name")
            .isEqualTo("Jane Smith")
            .jsonPath("$.email")
            .isEqualTo("jane.smith@example.com")
            .jsonPath("$.phoneNumber")
            .isEqualTo("+1-555-987-6543")
    }

    @Test
    fun `create user - returns 422 when required fields are blank`() {
        webTestClient
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("name" to "", "email" to ""))
            .exchange()
            .expectStatus()
            .isEqualTo(422)
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type")
            .isEqualTo("/errors/validation-failed")
            .jsonPath("$.status")
            .isEqualTo(422)
            .jsonPath("$.instance")
            .isEqualTo("/users")
            .jsonPath("$.correlationId")
            .isNotEmpty
            .jsonPath("$.errors.name")
            .isArray
            .jsonPath("$.errors.email")
            .isArray
    }

    @Test
    fun `find user - returns localized problem details when not found`() {
        webTestClient
            .get()
            .uri("/users/999999")
            .header("Accept-Language", "fr")
            .exchange()
            .expectStatus()
            .isNotFound
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type")
            .isEqualTo("/errors/user-not-found")
            .jsonPath("$.status")
            .isEqualTo(404)
            .jsonPath("$.detail")
            .isEqualTo("Utilisateur 999999 introuvable")
            .jsonPath("$.instance")
            .isEqualTo("/users/999999")
            .jsonPath("$.correlationId")
            .isNotEmpty
    }

    @Test
    fun `find user - hides framework details for invalid path values`() {
        webTestClient
            .get()
            .uri("/users/not-a-number")
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("$.type")
            .isEqualTo("about:blank")
            .jsonPath("$.status")
            .isEqualTo(400)
            .jsonPath("$.detail")
            .isEqualTo("The request could not be processed.")
    }

    @Test
    fun `error correlation ID matches the response header`() {
        val result =
            webTestClient
                .get()
                .uri("/users/999999")
                .exchange()
                .expectStatus()
                .isNotFound
                .expectBody(Map::class.java)
                .returnResult()
        val header = result.responseHeaders.getFirst("X-Correlation-Id")

        assertNotNull(header)
        assertEquals(header, result.responseBody!!["correlationId"])
    }

    @Test
    fun `swagger api docs describe user operations and success responses`() {
        webTestClient
            .get()
            .uri("/v3/api-docs")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.paths['/users'].post.operationId")
            .isEqualTo("createUser")
            .jsonPath("$.paths['/users'].post.responses['201'].content['application/json'].schema.\$ref")
            .isEqualTo("#/components/schemas/User")
            .jsonPath("$.paths['/users'].get.operationId")
            .isEqualTo("getAllUsers")
            .jsonPath("$.paths['/users'].get.responses['200'].content['application/json'].schema.items.\$ref")
            .isEqualTo("#/components/schemas/User")
            .jsonPath("$.paths['/users/{id}'].get.operationId")
            .isEqualTo("getUserById")
    }
}
