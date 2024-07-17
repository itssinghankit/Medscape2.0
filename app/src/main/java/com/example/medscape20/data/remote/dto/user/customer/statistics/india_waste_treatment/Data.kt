package com.example.medscape20.data.remote.dto.user.customer.statistics.india_waste_treatment

data class Data(
    val collected: Int,
    val landfilled: Int,
    val solid_waste_generated: Int,
    val state: String,
    val treated: Int
){
    constructor():this(0,0,0,"",0)
}