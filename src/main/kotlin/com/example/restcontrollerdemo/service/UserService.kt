package com.example.restcontrollerdemo.service

import com.example.restcontrollerdemo.domain.entity.User
import com.example.restcontrollerdemo.dto.ImageResponse
import com.example.restcontrollerdemo.dto.MeResponse
import com.example.restcontrollerdemo.dto.ProfileRequest
import com.example.restcontrollerdemo.dto.ProfileResponse
import com.example.restcontrollerdemo.dto.UserResponse
import com.example.restcontrollerdemo.exception.AppException
import com.example.restcontrollerdemo.repository.ImageRepository
import com.example.restcontrollerdemo.repository.ProfileRepository
import com.example.restcontrollerdemo.repository.UserRepository
import com.example.restcontrollerdemo.security.AuthenticatedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val imageRepository: ImageRepository,
) {
    suspend fun me(me: AuthenticatedUser): MeResponse = MeResponse(requireUser(me.userId))

    fun findAll(
        afterId: Long,
        limit: Int,
    ): Flow<UserResponse> = userRepository.findPage(afterId, limit).map(::UserResponse)

    suspend fun findById(id: Long): UserResponse = UserResponse(requireUser(id))

    suspend fun findProfile(userId: Long): ProfileResponse {
        val user = requireUser(userId)
        val profile =
            profileRepository.findByUserId(user.id!!)
                ?: throw AppException.NotFound("error.profile.not.found", arrayOf(userId))
        return ProfileResponse(profile)
    }

    /** Only the caller's own profile is writable; the target comes from the token, not the request. */
    suspend fun replaceMyProfile(
        me: AuthenticatedUser,
        request: ProfileRequest,
    ): ProfileResponse {
        val user = requireUser(me.userId)
        return ProfileResponse(profileRepository.upsert(user.id!!, request.bio, request.avatarUrl))
    }

    /** The existence check runs before the [Flow] is returned, so a 404 is raised before any byte is written. */
    suspend fun findImages(
        userId: Long,
        beforeId: Long,
        limit: Int,
    ): Flow<ImageResponse> {
        requireUser(userId)
        return imageRepository.findByUser(userId, beforeId, limit).map(::ImageResponse)
    }

    suspend fun requireUser(userId: Long): User =
        userRepository.findById(userId) ?: throw AppException.NotFound("error.user.not.found", arrayOf(userId))
}
