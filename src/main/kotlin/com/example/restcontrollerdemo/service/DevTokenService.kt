package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.dto.TokenResponse
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.UserRepository
import com.example.restcontrollerdemo.security.DevTokenIssuer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/** Passwordless token for local development. Exists only in the dev profile, which binds to loopback. */
@Service
@Profile("dev")
class DevTokenService(
    private val userRepository: UserRepository,
    private val issuer: DevTokenIssuer,
) {
    suspend fun issue(email: String): TokenResponse =
        userRepository.findByEmail(email)?.let(issuer::issue)
            ?: throw AppException.Unauthorized("error.auth.invalid")
}
