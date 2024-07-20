package com.example.medscape20.domain.usecase.user.customer.account.update_password

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class AccountUpdatePasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(newPassword:String,oldPassword:String,email:String)=userRepository.updatePasswordLoggedIn(newPassword,oldPassword,email)
}