package com.example.medscape20.domain.usecase.user.home.category

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class CategoryGetDataUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getCategoryData()
}