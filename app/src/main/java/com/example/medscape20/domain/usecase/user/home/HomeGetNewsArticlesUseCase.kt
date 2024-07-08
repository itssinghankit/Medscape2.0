package com.example.medscape20.domain.usecase.user.home

import com.example.medscape20.domain.repository.UserRepository
import javax.inject.Inject

class HomeGetNewsArticlesUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(params:Pair<String, Map<String,String>>) = userRepository.getNewsArticles(params)
}