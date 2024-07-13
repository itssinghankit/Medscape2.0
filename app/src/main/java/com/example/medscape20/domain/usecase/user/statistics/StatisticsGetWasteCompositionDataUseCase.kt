package com.example.medscape20.domain.usecase.user.statistics

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class StatisticsGetWasteCompositionDataUseCase@Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke() = userRepository.getStatsWasteComposition()
}