package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.Enterprise
import com.example.restcontrollerdemo.dto.EnterpriseRequest
import com.example.restcontrollerdemo.dto.EnterpriseWithTeams
import com.example.restcontrollerdemo.service.EnterpriseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
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
    @Operation(summary = "Create a new enterprise")
    suspend fun create(
        @Valid @RequestBody form: EnterpriseRequest,
    ): Enterprise = enterpriseService.create(form)

    @GetMapping("/{id}")
    @Operation(summary = "Find enterprise by ID")
    suspend fun findById(
        @PathVariable id: Long,
    ): Enterprise = enterpriseService.findById(id)

    @GetMapping
    @Operation(summary = "List all enterprises")
    fun findAll(): Flow<Enterprise> = enterpriseService.findAll()

    @GetMapping("/search")
    @Operation(summary = "Search enterprises by name (JOOQ MULTISET query — includes teams and users)")
    fun search(
        @RequestParam name: String,
    ): Flow<EnterpriseWithTeams> = enterpriseService.searchByNameJooq(name)
}
