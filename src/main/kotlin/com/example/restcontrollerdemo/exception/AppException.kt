package com.example.restcontrollerdemo.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponseException
import java.net.URI

sealed class AppException(
    status: HttpStatus,
    val messageKey: String,
    args: Array<Any> = emptyArray(),
    cause: Throwable? = null,
) : ErrorResponseException(status, ProblemDetail.forStatus(status), cause, messageKey, args) {
    init {
        body.detail = messageKey
        body.type = typeUri(messageKey)
    }

    class NotFound(
        messageKey: String,
        args: Array<Any> = emptyArray(),
        cause: Throwable? = null,
    ) : AppException(HttpStatus.NOT_FOUND, messageKey, args, cause)

    class Conflict(
        messageKey: String,
        args: Array<Any> = emptyArray(),
        cause: Throwable? = null,
    ) : AppException(HttpStatus.CONFLICT, messageKey, args, cause)

    open class BadRequest(
        messageKey: String,
        args: Array<Any> = emptyArray(),
        cause: Throwable? = null,
    ) : AppException(HttpStatus.BAD_REQUEST, messageKey, args, cause)

    class Unauthorized(
        messageKey: String,
        args: Array<Any> = emptyArray(),
        cause: Throwable? = null,
    ) : AppException(HttpStatus.UNAUTHORIZED, messageKey, args, cause)

    class Forbidden(
        messageKey: String,
        args: Array<Any> = emptyArray(),
        cause: Throwable? = null,
    ) : AppException(HttpStatus.FORBIDDEN, messageKey, args, cause)

    class ValidationErrors(
        val fieldErrors: Map<String, List<String>>,
        summaryKey: String = "error.validation_failed",
    ) : AppException(HttpStatus.UNPROCESSABLE_ENTITY, summaryKey)

    companion object {
        fun typeUri(messageKey: String): URI =
            URI.create("/errors/" + messageKey.removePrefix("error.").replace('.', '-').replace('_', '-'))
    }
}
