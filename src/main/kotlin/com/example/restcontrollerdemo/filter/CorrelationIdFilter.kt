package com.example.restcontrollerdemo.filter

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter : WebFilter {
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        exchange.response.headers.set(CORRELATION_ID_HEADER, UUID.randomUUID().toString())
        return chain.filter(exchange)
    }

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"

        fun of(exchange: ServerWebExchange): String =
            exchange.response.headers.getFirst(CORRELATION_ID_HEADER)
                ?: UUID.randomUUID().toString().also { exchange.response.headers.set(CORRELATION_ID_HEADER, it) }
    }
}
