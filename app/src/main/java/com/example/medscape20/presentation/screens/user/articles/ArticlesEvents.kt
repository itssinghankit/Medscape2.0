package com.example.medscape20.presentation.screens.user.articles

sealed class ArticlesEvents {
    data object ResetErrorMessage:ArticlesEvents()
    data class OnFilterSet(val category:String,val countryAbbreviation:String):ArticlesEvents()
    data object SearchArticles:ArticlesEvents()
}