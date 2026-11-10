package com.example.restcontrollerdemo.controller

import com.example.restcontrollerdemo.dto.CreatePostRequest
import com.example.restcontrollerdemo.dto.ImageResponse
import com.example.restcontrollerdemo.dto.PostDetails
import com.example.restcontrollerdemo.dto.PostSummary
import com.example.restcontrollerdemo.dto.UpdatePostRequest
import com.example.restcontrollerdemo.dto.UserResponse
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.security.AuthenticatedUser
import com.example.restcontrollerdemo.service.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import kotlinx.coroutines.flow.Flow
import org.hibernate.validator.constraints.Range
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Posts are visible to every user; writes are restricted to the author")
class PostController(
    private val postService: PostService,
) {
    @GetMapping
    @Operation(operationId = "getFeed", summary = "The feed, newest first, with image and like counts")
    fun feed(
        @RequestParam(required = false) @Positive(message = "{param.positive}") beforeId: Long?,
        @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
        @Range(min = 1, max = Pagination.MAX_LIMIT, message = "{param.limit.range}")
        limit: Int,
    ): Flow<PostSummary> = postService.feed(Pagination.before(beforeId), limit)

    /** Required key: without it, a retried or double-submitted create would publish the post twice. */
    @PostMapping
    @Operation(
        operationId = "createPost",
        summary = "Publish a post with its images, as the authenticated user",
        description =
            "A retry with the same Idempotency-Key and body returns the post created the first time " +
                "(header Idempotent-Replayed: true). The same key with a different body is a 409.",
        responses = [ApiResponse(responseCode = "201", description = "Created (or replayed)")],
    )
    suspend fun create(
        me: AuthenticatedUser,
        @Parameter(required = true, example = "3f8a2c4e-9b1d-4e6f-a5c7-2d9e8b1f0a3c")
        @RequestHeader(IDEMPOTENCY_KEY_HEADER, required = false)
        idempotencyKey: String?,
        @Valid @RequestBody request: CreatePostRequest,
    ): ResponseEntity<PostDetails> {
        val created = postService.create(me, validIdempotencyKey(idempotencyKey), request)
        return ResponseEntity
            .created(URI.create("/posts/${created.post.id}"))
            .header(REPLAYED_HEADER, created.replayed.toString())
            .body(created.post)
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getPostById", summary = "Get a post with its author, images and like count")
    suspend fun findById(
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ): PostDetails = postService.findById(id)

    @PutMapping("/{id}")
    @Operation(operationId = "updatePost", summary = "Edit a post's content (author only)")
    suspend fun update(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
        @Valid @RequestBody request: UpdatePostRequest,
    ): PostDetails = postService.update(me, id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deletePost", summary = "Delete a post and its images (author only)")
    suspend fun delete(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ) = postService.delete(me, id)

    @GetMapping("/{id}/images")
    @Operation(operationId = "getPostImages", summary = "List a post's images in display order (hasMany)")
    suspend fun findImages(
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ): Flow<ImageResponse> = postService.findImages(id)

    @GetMapping("/{id}/likes")
    @Operation(operationId = "getPostLikers", summary = "List users who like a post (manyToMany)")
    suspend fun findLikers(
        @PathVariable @Positive(message = "{param.positive}") id: Long,
        @RequestParam(required = false) @PositiveOrZero(message = "{param.positiveOrZero}") afterId: Long?,
        @RequestParam(defaultValue = Pagination.DEFAULT_LIMIT)
        @Range(min = 1, max = Pagination.MAX_LIMIT, message = "{param.limit.range}")
        limit: Int,
    ): Flow<UserResponse> = postService.findLikers(id, Pagination.after(afterId), limit)

    @PutMapping("/{id}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "likePost", summary = "Like a post as the authenticated user (idempotent)")
    suspend fun like(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ) = postService.like(me, id)

    @DeleteMapping("/{id}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "unlikePost", summary = "Remove the authenticated user's like (idempotent)")
    suspend fun unlike(
        me: AuthenticatedUser,
        @PathVariable @Positive(message = "{param.positive}") id: Long,
    ) = postService.unlike(me, id)

    /** Required: without it, a retried or double-submitted create would publish the post twice. */
    private fun validIdempotencyKey(key: String?): String {
        if (key.isNullOrBlank()) throw AppException.BadRequest("error.header.required", arrayOf(IDEMPOTENCY_KEY_HEADER))
        if (!IDEMPOTENCY_KEY.matches(key)) throw AppException.BadRequest("error.idempotency.key.invalid")
        return key
    }

    companion object {
        const val IDEMPOTENCY_KEY_HEADER = "Idempotency-Key"
        private val IDEMPOTENCY_KEY = Regex("[A-Za-z0-9_-]{8,128}")
        const val REPLAYED_HEADER = "Idempotent-Replayed"
    }
}
