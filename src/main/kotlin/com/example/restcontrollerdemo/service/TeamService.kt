package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.domain.entity.Team
import com.example.restcontrollerdemo.dto.TeamRequest
import com.example.restcontrollerdemo.dto.TeamSummary
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.EnterpriseRepository
import com.example.restcontrollerdemo.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val enterpriseRepository: EnterpriseRepository,
) {
    suspend fun create(form: TeamRequest): Team {
        val team =
            Team(
                enterpriseId = form.enterpriseId!!,
                name = form.name!!,
                description = form.description,
            )
        return teamRepository.save(team)
    }

    suspend fun findById(id: Long): Team = teamRepository.findById(id) ?: throw AppException.NotFound("error.team.not.found", id)

    fun findAll(): Flow<Team> = teamRepository.findAll()

    fun findByEnterprise(enterpriseId: Long): Flow<Team> = teamRepository.findAllByEnterpriseId(enterpriseId)

    suspend fun findTeamsWithEnterpriseInfo(): List<TeamSummary> {
        val teams = teamRepository.findAll().toList()
        if (teams.isEmpty()) return emptyList()

        val enterpriseIds = teams.map { it.enterpriseId }.distinct()
        val enterprisesById = enterpriseRepository.findAllById(enterpriseIds).toList().associateBy { it.id }

        return teams.map { team ->
            val enterprise =
                enterprisesById[team.enterpriseId]
                    ?: throw AppException.NotFound("error.enterprise.not.found", team.enterpriseId)
            TeamSummary(
                teamId = team.id,
                teamName = team.name,
                teamDescription = team.description,
                enterpriseName = enterprise.name,
                enterpriseEmail = enterprise.email,
            )
        }
    }
}
