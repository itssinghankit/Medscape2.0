package com.example.medscape20.presentation.screens.user.customer.account

sealed class AccountEvents {
    data object OnSignOutClicked: AccountEvents()
    data class OnAvatarUpdation(val url:String):AccountEvents()
    data object ResetErrorMessage:AccountEvents()
}