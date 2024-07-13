package com.example.medscape20.data.remote.dto.user.home.statistics.income_waste

data class StatisticsIncomeWasteDto(
    val `data`: List<Data>,
    val title: String
){
    constructor():this(emptyList(),"")
}