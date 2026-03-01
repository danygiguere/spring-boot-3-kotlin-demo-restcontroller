package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.domain.entity.TeamMember
import com.example.restcontrollerdemo.domain.entity.User
import com.example.restcontrollerdemo.dto.UserRequest
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.TeamMemberRepository
import com.example.restcontrollerdemo.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val teamMemberRepository: TeamMemberRepository,
) {
    suspend fun create(form: UserRequest): User {
        val user =
            User(
                name = form.name!!,
                email = form.email!!,
                phoneNumber = form.phoneNumber,
            )
        return userRepository.save(user)
    }

    suspend fun findById(id: Long): User = userRepository.findById(id) ?: throw AppException.NotFound("error.user.not.found", id)

    fun findAll(): Flow<User> = userRepository.findAll()

    suspend fun assignToTeam(
        userId: Long,
        teamId: Long,
    ): TeamMember = teamMemberRepository.save(TeamMember(teamId = teamId, userId = userId))

    fun findTeamsForUser(userId: Long): Flow<TeamMember> = teamMemberRepository.findAllByUserId(userId)

    fun findUsersForTeam(teamId: Long): Flow<TeamMember> = teamMemberRepository.findAllByTeamId(teamId)
}
