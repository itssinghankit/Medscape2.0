package com.example.medscape20.presentation.screens.user.account

sealed class AccountEvents {
    data object OnSignOutClicked:AccountEvents()
}