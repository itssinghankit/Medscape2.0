package com.example.medscape20.data.mapper

import com.example.medscape20.data.remote.dto.user.customer.articles.NewsResDto
import com.example.medscape20.domain.models.ArticleModel

fun NewsResDto.toArticleList(): List<ArticleModel> {

    val articleList = this.articles.filter {
        //they don't support web view
        it.source.name != "Business Insider" && it.source.name != "Grist" && it.source.id!=null
    }.map {
        ArticleModel(
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
