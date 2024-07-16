package com.example.medscape20.data.remote.dto.user.articles

data class NewsResDto(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)