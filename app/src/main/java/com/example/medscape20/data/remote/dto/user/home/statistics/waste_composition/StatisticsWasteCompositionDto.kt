package com.example.medscape20.data.remote.dto.user.home.statistics.waste_composition

data class StatisticsWasteCompositionDto(
    val `data`: List<Data>,
    val title: String
){
    constructor():this(emptyList(),"")
}