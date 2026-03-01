package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.domain.entity.Enterprise
import com.example.restcontrollerdemo.dto.EnterpriseRequest
import com.example.restcontrollerdemo.dto.EnterpriseWithTeams
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.EnterpriseJooqRepository
import com.example.restcontrollerdemo.repository.EnterpriseRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class EnterpriseService(
    private val enterpriseRepository: EnterpriseRepository,
    private val enterpriseJooqRepository: EnterpriseJooqRepository,
) {
    suspend fun create(form: EnterpriseRequest): Enterprise {
        val enterprise =
            Enterprise(
                name = form.name!!,
                phoneNumber = form.phoneNumber,
                website = form.website,
                email = form.email!!,
                description = form.description,
            )
        return enterpriseRepository.save(enterprise)
    }

    suspend fun findById(id: Long): Enterprise =
        enterpriseRepository.findById(id) ?: throw AppException.NotFound("error.enterprise.not.found", id)

    fun findAll(): Flow<Enterprise> = enterpriseRepository.findAll()

    fun searchByNameJooq(name: String): Flow<EnterpriseWithTeams> = enterpriseJooqRepository.searchByNameJooq(name)
}
