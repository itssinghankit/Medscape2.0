package com.example.medscape20.presentation.screens.auth.signup_details

sealed class SignupDetailsEvents {
    object OnNextClick : SignupDetailsEvents()
    object OnNavigationDone : SignupDetailsEvents()
    data class OnNameChanged(val name: String) : SignupDetailsEvents()
    data class OnMobileChanged(val mobile: String) : SignupDetailsEvents()
    data class OnAddressChanged(val address: String) : SignupDetailsEvents()
    data class OnGenderChanged(val gender: String) : SignupDetailsEvents()
    object OnMapBtnClicked : SignupDetailsEvents()
}