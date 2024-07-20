package com.example.medscape20.presentation.screens.user.customer.account

import com.example.medscape20.presentation.screens.user.customer.account.update_details.AccountUpdatedDetails

sealed class AccountEvents {
    data object OnSignOutClicked: AccountEvents()
    data class OnAvatarUpdation(val url:String):AccountEvents()
    data object ResetErrorMessage:AccountEvents()
    data class OnDetailsUpdation(val data:AccountUpdatedDetails):AccountEvents()
}