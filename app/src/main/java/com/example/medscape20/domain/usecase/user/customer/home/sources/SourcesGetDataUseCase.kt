package com.example.medscape20.domain.usecase.user.customer.home.sources

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class SourcesGetDataUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke()=userRepository.getSourcesData()
}