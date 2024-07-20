package com.example.medscape20.presentation.screens.user.customer.account.update_details

import com.example.medscape20.data.remote.dto.user.customer.home.HomeGetUserDataResDto

sealed class AccountUpdateDetailsEvents {
    data object ResetSnackbarMessage : AccountUpdateDetailsEvents()
    data class ValidateName(val name: String) : AccountUpdateDetailsEvents()
    data class ValidateEmail(val email: String) : AccountUpdateDetailsEvents()
    data class ValidateMobile(val mobile: String) : AccountUpdateDetailsEvents()
    data class SaveDetails(val data:HomeGetUserDataResDto): AccountUpdateDetailsEvents()
    data class OnAddressChanged(
        val address: String,
        val lat: Double,
        val lng: Double,
        val state: String?,
        val city: String?,
        val locality: String?
    ) : AccountUpdateDetailsEvents()
    data object OnUpdateDetailsClicked: AccountUpdateDetailsEvents()

}