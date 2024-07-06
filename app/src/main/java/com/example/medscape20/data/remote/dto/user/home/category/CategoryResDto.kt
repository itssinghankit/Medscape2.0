package com.example.medscape20.data.remote.dto.user.home.category

data class CategoryResDto(
    val image: String,
    val subtopic: List<Subtopic>,
    val topic: String
){
    constructor():this("", emptyList(),"")
}