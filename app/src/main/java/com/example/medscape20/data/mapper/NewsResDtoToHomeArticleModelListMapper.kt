package com.example.medscape20.data.mapper

import com.example.medscape20.data.remote.dto.user.home.articles.NewsResDto
import com.example.medscape20.domain.models.HomeArticleModel

fun NewsResDto.toArticleList(): List<HomeArticleModel> {

    val articleList = this.articles.map {
        HomeArticleModel(
            author = it.author,
            content = it.content,
            description = it.description,
            publishedAt = it.publishedAt,
            source = it.source.name,
            title = it.title,
            url = it.url,
            urlToImage = it.urlToImage
        )
    }
    return articleList
}