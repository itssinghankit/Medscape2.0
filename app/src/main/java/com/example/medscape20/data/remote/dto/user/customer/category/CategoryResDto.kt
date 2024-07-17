package com.example.medscape20.data.remote.dto.user.customer.category

data class CategoryResDto(
    val image: String,
    val subtopic: List<Subtopic>,
    val topic: String
){
    constructor():this("", emptyList(),"")
}