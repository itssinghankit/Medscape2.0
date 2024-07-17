package com.example.medscape20.data.remote.dto.user.customer.statistics.income_waste

data class Data(
    val income_level: String,
    val percentage: Int
){
    constructor():this("",0)
}