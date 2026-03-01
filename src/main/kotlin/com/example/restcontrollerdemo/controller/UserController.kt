package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.domain.entity.TeamMember
import com.example.restcontrollerdemo.domain.entity.User
import com.example.restcontrollerdemo.dto.UserRequest
import com.example.restcontrollerdemo.service.UserService
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
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    suspend fun create(
        @Valid @RequestBody form: UserRequest,
    ): User = userService.create(form)

    @GetMapping("/{id}")
    @Operation(summary = "Find user by ID")
    suspend fun findById(
        @PathVariable id: Long,
    ): User = userService.findById(id)

    @GetMapping
    @Operation(summary = "List all users")
    fun findAll(): Flow<User> = userService.findAll()

    @PostMapping("/{userId}/teams/{teamId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assign a user to a team")
    suspend fun assignToTeam(
        @PathVariable userId: Long,
        @PathVariable teamId: Long,
    ): TeamMember = userService.assignToTeam(userId, teamId)

    @GetMapping("/{userId}/teams")
    @Operation(summary = "Get all teams for a user")
    fun findTeams(
        @PathVariable userId: Long,
    ): Flow<TeamMember> = userService.findTeamsForUser(userId)
}
