package com.example.medscape20.data.remote.dto.Avatar

data class AvatarSaveDetailsDto(
    val name: String,
    val email: String,
    val gender: String,
    val mobile: String,
    val address: String,
    val avatar:String,
    val uid:String,
    val isDump:Boolean,
    val lat:Double,
    val lng:Double
)
