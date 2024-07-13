package com.example.medscape20.data.remote.dto.user.home.statistics.region_waste

data class Data(
    val percentage: Int,
    val region: String
){
    constructor():this(0,"")
}