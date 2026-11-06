package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.Enterprise
import com.example.restcontrollerdemo.dto.EnterpriseRequest
import com.example.restcontrollerdemo.dto.EnterpriseWithTeams
import com.example.restcontrollerdemo.service.EnterpriseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/enterprises")
@Tag(name = "Enterprises", description = "Enterprise management endpoints")
class EnterpriseController(
    private val enterpriseService: EnterpriseService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        operationId = "createEnterprise",
        summary = "Create an enterprise",
        responses = [
            ApiResponse(
                responseCode = "201",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Enterprise::class))],
            ),
        ],
    )
    suspend fun create(
        @Valid @RequestBody form: EnterpriseRequest,
    ): Enterprise = enterpriseService.create(form)

    @GetMapping("/{id}")
    @Operation(
        operationId = "getEnterpriseById",
        summary = "Get an enterprise by ID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Enterprise::class))],
            ),
        ],
    )
    suspend fun findById(
        @PathVariable id: Long,
    ): Enterprise = enterpriseService.findById(id)

    @GetMapping
    @Operation(
        operationId = "getAllEnterprises",
        summary = "Get all enterprises",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = Enterprise::class)),
                    ),
                ],
            ),
        ],
    )
    fun findAll(): Flow<Enterprise> = enterpriseService.findAll()

    @GetMapping("/search")
    @Operation(
        operationId = "searchEnterprises",
        summary = "Search enterprises by name",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = EnterpriseWithTeams::class)),
                    ),
                ],
            ),
        ],
    )
    suspend fun search(
        @RequestParam name: String,
    ): List<EnterpriseWithTeams> = enterpriseService.searchByName(name)
}
