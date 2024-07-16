package com.example.medscape20.domain.usecase.user.trash

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class TrashSetDumpUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(uid: String, updates:  HashMap<String, Any>) =
        userRepository.setTrashDump(uid, updates)
}