package com.example.medscape20.domain.usecase.user.customer.statistics

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class StatisticsGetIncomeWasteDataUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke() = userRepository.getStatsIncomeWaste()
}