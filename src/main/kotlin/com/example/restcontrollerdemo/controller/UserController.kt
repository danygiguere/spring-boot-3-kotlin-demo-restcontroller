package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.dto.MeResponse
import com.example.restcontrollerdemo.dto.PostSummary
import com.example.restcontrollerdemo.dto.ProfileRequest
import com.example.restcontrollerdemo.dto.ProfileResponse
import com.example.restcontrollerdemo.dto.UserResponse
import com.example.restcontrollerdemo.security.AuthenticatedUser
import com.example.restcontrollerdemo.service.PostService
import com.example.restcontrollerdemo.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import kotlinx.coroutines.flow.Flow
import org.hibernate.validator.constraints.Range
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Users of the caller's enterprise only")
class UserController(
    private val userService: UserService,
    private val postService: PostService,
) {
    @GetMapping("/me")
    @Operation(operationId = "getMe", summary = "Get the authenticated user")
    suspend fun me(me: AuthenticatedUser): MeResponse = userService.me(me)

    @PutMapping("/me/profile")
    @Operation(operationId = "replaceMyProfile", summary = "Create or replace the authenticated user's profile")
    suspend fun replaceMyProfile(
        me: AuthenticatedUser,
        @Valid @RequestBody request: ProfileRequest,
    ): ProfileResponse = userService.replaceMyProfile(me, request)

    @GetMapping
    @Operation(operationId = "getUsers", summary = "List users of the caller's enterprise, oldest first")
    fun findAll(
        me: AuthenticatedUser,
        @RequestParam(required = false) @PositiveOrZero(message = "{param.positiveOrZero}") afterId: Long?,
        @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
        @Range(min = 1, max = Pagination.MAX_LIMIT, message = "{param.limit.range}")
        limit: Int,
    ): Flow<UserResponse> = userService.findAll(me, Pagination.after(afterId), limit)

    @GetMapping("/{id}")
    @Operation(operationId = "getUserById", summary = "Get a user of the caller's enterprise")
    suspend fun findById(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ): UserResponse = userService.findById(me, id)

    @GetMapping("/{id}/profile")
    @Operation(operationId = "getUserProfile", summary = "Get a user's profile (hasOne)")
    suspend fun findProfile(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ): ProfileResponse = userService.findProfile(me, id)

    @GetMapping("/{id}/posts")
    @Operation(operationId = "getUserPosts", summary = "List a user's posts, newest first (hasMany)")
    suspend fun findPosts(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
        @RequestParam(required = false) @Positive(message = "{param.positive}") beforeId: Long?,
        @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
        @Range(min = 1, max = Pagination.MAX_LIMIT, message = "{param.limit.range}")
        limit: Int,
    ): List<PostSummary> = postService.findByAuthor(me, id, Pagination.before(beforeId), limit)

    @GetMapping("/{id}/liked-posts")
    @Operation(operationId = "getUserLikedPosts", summary = "List posts a user likes, newest first (manyToMany)")
    suspend fun findLikedPosts(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
        @RequestParam(required = false) @Positive(message = "{param.positive}") beforeId: Long?,
        @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
        @Range(min = 1, max = Pagination.MAX_LIMIT, message = "{param.limit.range}")
        limit: Int,
    ): List<PostSummary> = postService.findLikedBy(me, id, Pagination.before(beforeId), limit)
}
