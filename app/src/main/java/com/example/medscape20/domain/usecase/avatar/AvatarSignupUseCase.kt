package com.example.medscape20.domain.usecase.avatar

import com.example.medscape20.data.remote.dto.Avatar.AvatarSignupDto
import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class AvatarSignupUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(avatarSignupDto: AvatarSignupDto) =
        authRepository.signup(avatarSignupDto)

}