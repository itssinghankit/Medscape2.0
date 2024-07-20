package com.example.medscape20.presentation.screens.user.customer.account.change_password

sealed class AccountChangePasswordEvents {
    data object ResetMessage:AccountChangePasswordEvents()
    data class ValidatePassword(val password:String):AccountChangePasswordEvents()
    data object OnChangePasswordClicked:AccountChangePasswordEvents()
}