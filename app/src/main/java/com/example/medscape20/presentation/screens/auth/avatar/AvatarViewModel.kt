package com.example.medscape20.presentation.screens.auth.avatar

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AvatarViewModel @Inject constructor() : ViewModel() {

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    fun event(action: AvatarEvents) {
        when (action) {
            is AvatarEvents.OnAvatarSelected -> {
                 _avatarUri.update {
                    action.uri
                }
            }

            AvatarEvents.OnSignupClicked -> TODO()
        }
    }


}