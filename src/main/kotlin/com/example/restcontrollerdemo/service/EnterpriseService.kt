package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.dto.EnterpriseResponse
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.EnterpriseRepository
import com.example.restcontrollerdemo.security.AuthenticatedUser
import org.springframework.stereotype.Service

@Service
class EnterpriseService(
    private val enterpriseRepository: EnterpriseRepository,
) {
    /** The caller's enterprise: the only one they can see. */
    suspend fun current(me: AuthenticatedUser): EnterpriseResponse =
        enterpriseRepository.findById(me.enterpriseId)?.let(::EnterpriseResponse)
            ?: throw AppException.NotFound("error.enterprise.not.found")
}
