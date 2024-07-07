package com.example.medscape20.domain.usecase.user.home

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class HomeGetUserDataUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uri: String) = userRepository.getUserData(uri)
}