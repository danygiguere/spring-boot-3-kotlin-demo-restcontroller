package com.example.restcontrollerdemo.repository

import com.example.restcontrollerdemo.domain.entity.Team
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TeamRepository : CoroutineCrudRepository<Team, Long> {
    fun findAllByEnterpriseId(enterpriseId: Long): Flow<Team>
}
