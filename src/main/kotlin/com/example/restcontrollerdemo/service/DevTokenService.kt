package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.dto.TokenResponse
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.UserRepository
import com.example.restcontrollerdemo.security.DevTokenEnabled
import com.example.restcontrollerdemo.security.DevTokenIssuer
import org.springframework.stereotype.Service

/** Passwordless token for local development. Exists only with DEV_TOKEN_ENABLED, which requires a loopback bind. */
@Service
@DevTokenEnabled
class DevTokenService(
    private val userRepository: UserRepository,
    private val issuer: DevTokenIssuer,
) {
    suspend fun issue(email: String): TokenResponse =
        userRepository.findByEmail(email)?.let(issuer::issue)
            ?: throw AppException.Unauthorized("error.auth.invalid")
}
