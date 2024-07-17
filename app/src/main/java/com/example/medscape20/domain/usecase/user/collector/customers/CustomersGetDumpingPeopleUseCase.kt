package com.example.medscape20.domain.usecase.user.collector.customers

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class CustomersGetDumpingPeopleUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getDumpingPeoples()
}