package com.example.medscape20.data.remote.dto.user.customer.home.sources

import java.io.Serializable

data class SourcesDto(
    val description: String,
    val image: String,
    val subtopics: List<Subtopic>,
    val topic: String
):Serializable{
    constructor():this("","", emptyList(),"")
}