package com.example.medscape20.presentation.screens.user.statistics

data class StatisticsGlobalPieModel(
    val value: List<String>,
    val percent: List<Int>,
    val title: String,
    val bgColor:String,
    val strokeColor:String
)
