package com.example.medscape20.domain.usecase.user.customer.account.change_password

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class AccountUpdatePasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(password:String)=userRepository.updatePasswordLoggedIn(password)
}