package com.example.medscape20.data.remote.dto.user.home

data class HomeGetUserDataResDto(
    val address: String?=null,
    val avatar: String?=null,
    val city: String?=null,
    val dump: Boolean?=null,
    val email: String?=null,
    val gender: String?=null,
    val lat: Double?=null,
    val lng: Double?=null,
    val mobile: String?=null,
    val name: String?=null,
    val state: String?=null,
    val uid: String?=null,
    val locality: String?=null
)