package com.example.medscape20.presentation.screens.auth.signup

sealed class SignupEvents {
    object OnNextClick: SignupEvents()
    object OnNavigationDone: SignupEvents()
    data class OnEmailChanged(val email:String): SignupEvents()
    data class OnPasswordChanged(val password:String): SignupEvents()
}