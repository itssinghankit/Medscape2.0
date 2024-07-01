package com.example.medscape20.presentation.screens.auth.avatar

import android.net.Uri

sealed class AvatarEvents {
    data class OnSignupClicked(val args: AvatarFragmentArgs): AvatarEvents()
    data class OnAvatarSelected(val uri: Uri): AvatarEvents()
    object OnErrorShown: AvatarEvents()
    object OnNavigationDone:AvatarEvents()
}