package com.example.medscape20.data.remote.dto.user.statistics.india_waste_treatment

data class StatisticsIndiaWasteTreatmentDto(
    val `data`: List<Data>,
    val title: String
){
    constructor():this(emptyList(),"")
}