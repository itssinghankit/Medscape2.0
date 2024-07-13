package com.example.medscape20.data.remote.dto.user.home.statistics.waste_composition

data class Data(
    val category: String,
    val percentage: Int
){
    constructor():this("",0)
}