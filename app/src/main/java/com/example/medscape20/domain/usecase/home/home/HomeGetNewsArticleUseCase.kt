package com.example.medscape20.domain.usecase.home.home

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class HomeGetNewsArticleUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke() = userRepository.getHomeNewsArticles()
}