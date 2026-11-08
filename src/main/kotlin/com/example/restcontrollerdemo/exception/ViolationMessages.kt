package com.example.restcontrollerdemo.exception

import jakarta.validation.ConstraintViolation
import jakarta.validation.MessageInterpolator
import jakarta.validation.ValidationException
import jakarta.validation.ValidatorFactory
import jakarta.validation.metadata.ConstraintDescriptor
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.stereotype.Component
import org.springframework.validation.ObjectError
import org.springframework.validation.method.ParameterValidationResult
import java.util.Locale

/**
 * Localizes Bean Validation messages in the REQUEST's locale.
 *
 * `@Valid` runs during argument resolution, where WebFlux has no locale context: the message is interpolated in the
 * JVM default locale. Each violation keeps its template (`{post.content.required}`), so it is interpolated again here
 * with the locale resolved from `Accept-Language`.
 */
@Component
class ViolationMessages(
    validatorFactory: ValidatorFactory,
    private val messageSource: MessageSource,
) {
    private val interpolator: MessageInterpolator = validatorFactory.messageInterpolator

    /** A `@Valid @RequestBody` field error. */
    fun of(
        error: ObjectError,
        locale: Locale,
    ): String =
        if (error.contains(ConstraintViolation::class.java)) of(error.unwrap(ConstraintViolation::class.java), locale) else fallback(locale)

    /** A method-validation error on a `@RequestParam`, `@PathVariable` or `@RequestHeader`. */
    fun of(
        result: ParameterValidationResult,
        error: MessageSourceResolvable,
        locale: Locale,
    ): String =
        try {
            of(result.unwrap(error, ConstraintViolation::class.java), locale)
        } catch (notAViolation: IllegalArgumentException) {
            fallback(locale)
        }

    fun of(
        violation: ConstraintViolation<*>,
        locale: Locale,
    ): String = interpolator.interpolate(violation.messageTemplate, ViolationContext(violation), locale)

    private fun fallback(locale: Locale): String = messageSource.getMessage("validation.invalid.value", null, "Invalid value", locale)!!

    private class ViolationContext(
        private val violation: ConstraintViolation<*>,
    ) : MessageInterpolator.Context {
        override fun getConstraintDescriptor(): ConstraintDescriptor<*> = violation.constraintDescriptor

        override fun getValidatedValue(): Any? = violation.invalidValue

        override fun <T : Any?> unwrap(type: Class<T>): T {
            if (type.isInstance(this)) return type.cast(this)
            throw ValidationException("Cannot unwrap to ${type.name}")
        }
    }
}
