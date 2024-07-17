package com.example.medscape20.domain.usecase.auth.avatar

import com.example.medscape20.data.remote.dto.avatar.AvatarSignupReqDto
import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class AvatarSignupUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(avatarSignupReqDto: AvatarSignupReqDto) =
        authRepository.signup(avatarSignupReqDto)

}