package com.example.medscape20.domain.usecase.user.customer.account

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class AccountGetUserDataUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid:String) = userRepository.getUserData(uid)
}