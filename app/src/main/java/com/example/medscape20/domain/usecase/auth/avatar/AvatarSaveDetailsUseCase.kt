package com.example.medscape20.domain.usecase.auth.avatar

import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveDetailsReqDto
import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class AvatarSaveDetailsUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(avatarSaveDetailsDto: AvatarSaveDetailsReqDto) =
        authRepository.saveDetails(avatarSaveDetailsDto)
}