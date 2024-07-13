package com.example.medscape20.domain.usecase.user.statistics

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject


class StatisticsGetRegionWasteDataUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke() = userRepository.getStatsRegionWaste()
}