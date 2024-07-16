package com.example.medscape20.data.remote.dto.user.statistics.region_waste

data class StatisticsRegionWasteDto(
    val `data`: List<Data>,
    val title: String
){
    constructor():this(data= emptyList(),title="")
}