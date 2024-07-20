package com.example.medscape20.domain.usecase.user.customer.account.change_avatar

import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class AccountUpdateAvatarUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(avatarSaveAvatarDto: AvatarSaveAvatarReqDto) =
        authRepository.saveAvatar(avatarSaveAvatarDto)
}