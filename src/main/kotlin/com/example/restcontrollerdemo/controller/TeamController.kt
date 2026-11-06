package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.Team
import com.example.restcontrollerdemo.dto.TeamRequest
import com.example.restcontrollerdemo.dto.TeamSummary
import com.example.restcontrollerdemo.service.TeamService
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
    @Operation(
        operationId = "createTeam",
        summary = "Create a team",
        responses = [
            ApiResponse(
                responseCode = "201",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Team::class))],
            ),
        ],
    )
    suspend fun create(
        @Valid @RequestBody form: TeamRequest,
    ): Team = teamService.create(form)

    @GetMapping("/{id}")
    @Operation(
        operationId = "getTeamById",
        summary = "Get a team by ID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = Team::class))],
            ),
        ],
    )
    suspend fun findById(
        @PathVariable id: Long,
    ): Team = teamService.findById(id)

    @GetMapping
    @Operation(
        operationId = "getAllTeams",
        summary = "Get all teams",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = Team::class)),
                    ),
                ],
            ),
        ],
    )
    fun findAll(): Flow<Team> = teamService.findAll()

    @GetMapping("/enterprise/{enterpriseId}")
    @Operation(
        operationId = "getTeamsByEnterprise",
        summary = "Get teams by enterprise ID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = Team::class)),
                    ),
                ],
            ),
        ],
    )
    fun findByEnterprise(
        @PathVariable enterpriseId: Long,
    ): Flow<Team> = teamService.findByEnterprise(enterpriseId)

    @GetMapping("/summary")
    @Operation(
        operationId = "getTeamSummary",
        summary = "Get teams with enterprise info",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = TeamSummary::class)),
                    ),
                ],
            ),
        ],
    )
    suspend fun findSummary(): List<TeamSummary> = teamService.findTeamsWithEnterpriseInfo()
}
