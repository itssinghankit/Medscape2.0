package com.example.medscape20.data.remote.dto.user.types

data class TypesResDtoItem(
    val image: String,
    val subtopic: List<Subtopic>,
    val topic: String
)