package com.example.medscape20.data.remote.dto.avatar

data class AvatarSaveDetailsReqDto(
    val name: String,
    val email: String,
    val gender: String,
    val mobile: String,
    val address: String,
    val avatar:String,
    val uid:String,
    val isDump:Boolean,
    val lat:Double,
    val lng:Double,
    val state:String?,
    val city:String?,
    val locality:String?,
    val metal:Boolean?,
    val general:Boolean?,
    val medical:Boolean?,
    val plastic:Boolean
)
