package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.TeamMember
import com.example.restcontrollerdemo.domain.entity.User
import com.example.restcontrollerdemo.dto.UserRequest
import com.example.restcontrollerdemo.service.UserService
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
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        operationId = "createUser",
        summary = "Create a user",
        responses = [
            ApiResponse(
                responseCode = "201",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = User::class))],
            ),
        ],
    )
    suspend fun create(
        @Valid @RequestBody form: UserRequest,
    ): User = userService.create(form)

    @GetMapping("/{id}")
    @Operation(
        operationId = "getUserById",
        summary = "Get a user by ID",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = User::class))],
            ),
        ],
    )
    suspend fun findById(
        @PathVariable id: Long,
    ): User = userService.findById(id)

    @GetMapping
    @Operation(
        operationId = "getAllUsers",
        summary = "Get all users",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = User::class)),
                    ),
                ],
            ),
        ],
    )
    fun findAll(): Flow<User> = userService.findAll()

    @PostMapping("/{userId}/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        operationId = "assignUserToTeam",
        summary = "Assign a user to a team",
        responses = [
            ApiResponse(
                responseCode = "201",
                content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = TeamMember::class))],
            ),
        ],
    )
    suspend fun assignToTeam(
        @PathVariable userId: Long,
        @PathVariable teamId: Long,
    ): TeamMember = userService.assignToTeam(userId, teamId)

    @GetMapping("/{userId}/teams")
    @Operation(
        operationId = "getUserTeams",
        summary = "Get teams for a user",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(schema = Schema(implementation = TeamMember::class)),
                    ),
                ],
            ),
        ],
    )
    fun findTeams(
        @PathVariable userId: Long,
    ): Flow<TeamMember> = userService.findTeamsForUser(userId)
}
