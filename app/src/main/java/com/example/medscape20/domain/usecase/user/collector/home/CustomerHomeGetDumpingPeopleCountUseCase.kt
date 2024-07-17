package com.example.medscape20.domain.usecase.user.collector.home

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class CustomerHomeGetDumpingPeopleCountUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(city:String,state:String)=userRepository.getDumpingPeopleCount(city,state)
}