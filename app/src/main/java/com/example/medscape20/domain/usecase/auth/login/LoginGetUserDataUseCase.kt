package com.example.medscape20.domain.usecase.auth.login

import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class LoginGetUserDataUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(uri: String) = authRepository.getUserData(uri)
}