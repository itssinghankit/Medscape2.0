package com.example.medscape20.presentation.screens.auth.avatar

import android.net.Uri

sealed class AvatarEvents {
    object OnSignupClicked: AvatarEvents()
    object On
    data class OnAvatarSelected(val uri: Uri): AvatarEvents()
}