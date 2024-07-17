package com.example.medscape20.domain.usecase.auth.avatar

import com.example.medscape20.data.remote.dto.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.domain.repository.AuthRepository
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AvatarSaveAvatarUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        avatarSaveAvatarDto: AvatarSaveAvatarReqDto
    ): Flow<ApiResult<String, DataError.Network>> = authRepository.saveAvatar(avatarSaveAvatarDto)

}