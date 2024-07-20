package com.example.medscape20.presentation.screens.user.customer.account.update_password

sealed class AccountChangePasswordEvents {
    data object ResetMessage:AccountChangePasswordEvents()
    data class ValidateNewPassword(val newPassword:String):AccountChangePasswordEvents()
    data class ValidateCurrPassword(val currPassword:String):AccountChangePasswordEvents()
    data object OnChangePasswordClicked:AccountChangePasswordEvents()
    data class SaveEmail(val email:String):AccountChangePasswordEvents()
}