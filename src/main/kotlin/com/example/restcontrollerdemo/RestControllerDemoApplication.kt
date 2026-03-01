package com.example.restcontrollerdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    excludeName = [
        "org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration",
        "org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration",
    ],
)
class RestControllerDemoApplication

fun main(args: Array<String>) {
    runApplication<RestControllerDemoApplication>(*args)
}
