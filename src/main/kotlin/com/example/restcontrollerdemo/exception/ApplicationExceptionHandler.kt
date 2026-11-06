package com.example.restcontrollerdemo.exception

import com.example.restcontrollerdemo.filter.CorrelationIdFilter
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import java.net.URI
import java.util.Locale
import java.util.concurrent.CancellationException

@Component
@Order(-2)
class ApplicationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val messageSource: MessageSource,
) : WebExceptionHandler {
    private val logger = KotlinLogging.logger {}

    private fun logAppException(
        ex: AppException,
        correlationId: String,
    ) {
        val label = "AppException.${ex::class.simpleName}"
        if (ex.cause != null) {
            logger.warn(ex) { "[$correlationId] $label: ${ex.messageKey}" }
        } else {
            logger.warn { "[$correlationId] $label: ${ex.messageKey}" }
        }
    }

    private fun resolveStatusMessage(
        status: HttpStatusCode,
        locale: Locale,
    ): String {
        val fallback =
            if (status.is4xxClientError) {
                "The request could not be processed."
            } else {
                "An unexpected error occurred. Please try again later."
            }
        return messageSource.getMessage("error.${status.value()}", null, fallback, locale) ?: fallback
    }

    private fun problemOf(
        status: HttpStatusCode,
        detail: String,
        errors: Map<String, List<String>>? = null,
        typeKey: String? = null,
    ): ProblemDetail =
        ProblemDetail.forStatusAndDetail(status, detail).apply {
            typeKey?.let { type = AppException.typeUri(it) }
            errors?.let { setProperty("errors", it) }
        }

    override fun handle(
        exchange: ServerWebExchange,
        ex: Throwable,
    ): Mono<Void> {
        val correlationId = CorrelationIdFilter.of(exchange)
        val locale = exchange.localeContext.locale ?: Locale.ENGLISH

        val problem =
            when (ex) {
                is AppException.ValidationErrors -> {
                    logAppException(ex, correlationId)
                    val resolvedErrors =
                        ex.fieldErrors.mapValues { (_, keys) ->
                            keys.map { key -> messageSource.getMessage(key, null, key, locale) ?: key }
                        }
                    ex.updateAndGetBody(messageSource, locale).apply { setProperty("errors", resolvedErrors) }
                }

                is AppException -> {
                    logAppException(ex, correlationId)
                    ex.updateAndGetBody(messageSource, locale)
                }

                is ConstraintViolationException -> {
                    val summary =
                        messageSource.getMessage("error.validation_failed", null, "Validation failed", locale)
                            ?: "Validation failed"
                    val fieldErrors =
                        ex.constraintViolations
                            .groupBy({ it.propertyPath.toString() }, { it.message })
                    logger.warn {
                        "[$correlationId] Validation failed on ${exchange.request.method} ${exchange.request.path}: " +
                            fieldErrors.entries.joinToString(", ") { (field, messages) -> "$field: ${messages.joinToString("; ")}" }
                    }
                    problemOf(HttpStatus.UNPROCESSABLE_ENTITY, summary, fieldErrors, "error.validation_failed")
                }

                is WebExchangeBindException -> {
                    val summary =
                        messageSource.getMessage("error.validation_failed", null, "Validation failed", locale)
                            ?: "Validation failed"
                    val fieldErrors =
                        ex.bindingResult.fieldErrors
                            .groupBy({ it.field }, {
                                messageSource.getMessage(it, locale)
                                    ?: messageSource.getMessage("validation.invalid.value", null, "Invalid value", locale)
                                    ?: "Invalid value"
                            })
                    logger.warn {
                        "[$correlationId] Validation failed on ${exchange.request.method} ${exchange.request.path}: " +
                            fieldErrors.entries.joinToString(", ") { (field, messages) -> "$field: ${messages.joinToString("; ")}" }
                    }
                    problemOf(HttpStatus.UNPROCESSABLE_ENTITY, summary, fieldErrors, "error.validation_failed")
                }

                is ResponseStatusException -> {
                    if (ex.statusCode.is4xxClientError) {
                        logger.warn { "[$correlationId] ${ex.javaClass.simpleName}: ${ex.reason}" }
                        problemOf(ex.statusCode, resolveStatusMessage(ex.statusCode, locale))
                    } else {
                        logger.error(ex) { "[$correlationId] ${ex.javaClass.simpleName}: ${ex.reason}" }
                        problemOf(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            resolveStatusMessage(HttpStatus.INTERNAL_SERVER_ERROR, locale),
                        )
                    }
                }

                is CancellationException -> return Mono.empty()

                else -> {
                    logger.error(ex) { "[$correlationId] ${ex.javaClass.simpleName}: ${ex.message}" }
                    problemOf(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        resolveStatusMessage(HttpStatus.INTERNAL_SERVER_ERROR, locale),
                    )
                }
            }

        if (problem.title.isNullOrBlank()) {
            problem.title = HttpStatus.resolve(problem.status)?.reasonPhrase ?: "Error"
        }
        problem.instance = URI.create(exchange.request.path.value())
        problem.setProperty("correlationId", correlationId)

        exchange.response.statusCode = HttpStatusCode.valueOf(problem.status)
        exchange.response.headers.contentType = MediaType.APPLICATION_PROBLEM_JSON

        val buffer = exchange.response.bufferFactory().wrap(objectMapper.writeValueAsBytes(problem))
        return exchange.response.writeWith(Mono.just(buffer))
    }
}
