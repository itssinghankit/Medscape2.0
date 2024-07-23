package com.example.medscape20.data.remote.dto.user.customer.home.sources

import java.io.Serializable

data class Subtopic(
    val sub_description: String,
    val subtopic: String
):Serializable{
    constructor():this("","")
}