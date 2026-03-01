package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.Team
import com.example.restcontrollerdemo.dto.TeamRequest
import com.example.restcontrollerdemo.dto.TeamSummary
import com.example.restcontrollerdemo.service.TeamService
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/teams")
@Tag(name = "Teams", description = "Team management endpoints")
class TeamController(
    private val teamService: TeamService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new team")
    suspend fun create(
        @Valid @RequestBody form: TeamRequest,
    ): Team = teamService.create(form)

    @GetMapping("/{id}")
    @Operation(summary = "Find team by ID")
    suspend fun findById(
        @PathVariable id: Long,
    ): Team = teamService.findById(id)

    @GetMapping
    @Operation(summary = "List all teams")
    fun findAll(): Flow<Team> = teamService.findAll()

    @GetMapping("/enterprise/{enterpriseId}")
    @Operation(summary = "Find teams by enterprise ID")
    fun findByEnterprise(
        @PathVariable enterpriseId: Long,
    ): Flow<Team> = teamService.findByEnterprise(enterpriseId)

    @GetMapping("/summary")
    @Operation(summary = "List all teams with enterprise info (JOOQ join query)")
    fun findSummary(): Flow<TeamSummary> = teamService.findTeamsWithEnterpriseInfo()
}
