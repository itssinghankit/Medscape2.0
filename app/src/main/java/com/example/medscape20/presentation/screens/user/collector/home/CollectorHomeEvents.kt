package com.example.medscape20.presentation.screens.user.collector.home

sealed class CollectorHomeEvents {
    data object ResetErrorMessage:CollectorHomeEvents()
    data object OnLogOutClicked:CollectorHomeEvents()
}