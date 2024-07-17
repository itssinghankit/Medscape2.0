package com.example.medscape20.domain.usecase.user.collector.home

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class CustomerHomeGetUserDataUseCase @Inject constructor(val userRepository: UserRepository) {
    suspend operator fun invoke(uid:String)= userRepository.getUserData(uid)
}