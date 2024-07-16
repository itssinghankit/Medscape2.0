package com.example.medscape20.data.remote.dto.user.trash

data class TrashSetDumpReqDto(
    val dump:Boolean,
    val metal:Boolean,
    val general:Boolean,
    val plastic:Boolean,
    val medical:Boolean
)
