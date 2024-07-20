package com.example.medscape20.presentation.screens.user.customer.home

sealed class HomeEvents {
    data object ResetErrorMessage: HomeEvents()
    data class ShowNewsArticle(val url:String): HomeEvents()
    data class GetNewsArticles(val searchTopic:String): HomeEvents()
    data class OnAvatarUpdation(val url: String):HomeEvents()

}