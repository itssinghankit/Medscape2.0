package com.example.medscape20.domain.usecase.user.articles

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class ArticlesGetNewsUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getNewsArticles()
}