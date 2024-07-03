package com.example.medscape20.presentation.screens.auth.login

sealed class LoginEvents {
    object OnLoginClicked:LoginEvents()
    object  OnNavigationDone:LoginEvents()
    data class OnEmailChanged(val email:String):LoginEvents()
    data class OnPassChange(val pass:String):LoginEvents()
    object OnErrorShown:LoginEvents()
}