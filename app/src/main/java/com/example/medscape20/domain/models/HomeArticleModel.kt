package com.example.medscape20.domain.models


data class HomeArticleModel(
    val author: String?="",
    val content: String?="",
    val description: String?="",
    val publishedAt: String?="",
    val source: String?="",
    val title: String?="",
    val url: String?="",
    val urlToImage: String?=""
)
