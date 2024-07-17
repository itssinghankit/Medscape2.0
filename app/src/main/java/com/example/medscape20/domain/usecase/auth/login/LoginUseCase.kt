package com.example.medscape20.domain.usecase.auth.login

import com.example.medscape20.data.remote.dto.login.LoginReqDto
import com.example.medscape20.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(loginReqDto: LoginReqDto) = authRepository.login(loginReqDto)
}