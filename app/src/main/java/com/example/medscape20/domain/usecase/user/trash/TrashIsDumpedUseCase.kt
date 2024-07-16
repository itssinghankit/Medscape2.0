package com.example.medscape20.domain.usecase.user.trash

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class TrashIsDumpedUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid: String) = userRepository.isDumped(uid)
}