package com.example.medscape20.domain.usecase.user.customer.account.update_avatar

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class AccountUpdateAvatarInDbUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(uid:String,updates:HashMap<String,Any>)= userRepository.updateDatabase(uid, updates)
}