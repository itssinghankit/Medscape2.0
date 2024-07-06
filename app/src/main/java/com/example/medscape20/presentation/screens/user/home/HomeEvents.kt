package com.example.medscape20.presentation.screens.user.home

sealed class HomeEvents {
    object GetUserData : HomeEvents()
    object ResetErrorMessage:HomeEvents()
}