package com.example.medscape20.data.remote.dto.user.category

data class Subtopic(
    val description: String,
    val subtopic_name: String
){
    constructor():this("","")
}