package com.example.medscape20.domain.usecase.user.customer.trash

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class TrashUpdateDumpUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(uid: String, updates:  HashMap<String, Any>) =
        userRepository.updateDatabase(uid, updates)
}