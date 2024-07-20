package com.example.medscape20.presentation.screens.user.customer.account.change_avatar

import android.net.Uri

sealed class AccountChangeAvatarEvents {
    data object ResetErrorMessage:AccountChangeAvatarEvents()
    data class OnNewAvatarSelected(val avatar: Uri):AccountChangeAvatarEvents()
    data object OnUpdateAvatarClicked:AccountChangeAvatarEvents()
}