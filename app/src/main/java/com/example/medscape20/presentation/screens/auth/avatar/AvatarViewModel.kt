package com.example.medscape20.presentation.screens.auth.avatar

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveDetailsReqDto
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSignupReqDto
import com.example.medscape20.domain.usecase.auth.avatar.AvatarSaveAvatarUseCase
import com.example.medscape20.domain.usecase.auth.avatar.AvatarSaveDetailsUseCase
import com.example.medscape20.domain.usecase.auth.avatar.AvatarSignupUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AvatarState(
    val isError: Boolean = false,
    val errorMessage: Int? = null,
    val avatarUri: Uri? = null,
    val isLoading: Boolean = false,
    val navigateToUserScreen: Boolean = false
)

@HiltViewModel
class AvatarViewModel @Inject constructor(
    private val avatarSignupUseCase: AvatarSignupUseCase,
    private val avatarSaveAvatarUseCase: AvatarSaveAvatarUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val avatarSaveDetailsUseCase: AvatarSaveDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AvatarState())
    val state: StateFlow<AvatarState> = _state.asStateFlow()

    fun event(action: AvatarEvents) {
        when (action) {
            is AvatarEvents.OnAvatarSelected -> {
                _state.update {
                    it.copy(avatarUri = action.uri)
                }
            }

            is AvatarEvents.OnSignupClicked -> {
                if (_state.value.avatarUri != null) {
                    signup(action.args)
                } else {
                    _state.update {
                        it.copy(isError = true, errorMessage = R.string.error_empty_avatar)
                    }
                }
            }

            AvatarEvents.OnErrorShown -> {
                _state.update {
                    it.copy(isError = false, errorMessage = null)
                }
            }

            AvatarEvents.OnNavigationDone -> {
                _state.update {
                    it.copy(navigateToUserScreen = false)
                }
            }
        }
    }


    private fun signup(args: AvatarFragmentArgs) {
        viewModelScope.launch {
            val signupDto = AvatarSignupReqDto(args.email, args.password)
            avatarSignupUseCase(signupDto).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.ALREADY_CREATED -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        isError = true,
                                        errorMessage = R.string.error_user_already_registered
                                    )
                                }
                            }

                            else -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        isError = true,
                                        errorMessage = R.string.error_internal_server
                                    )
                                }
                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is ApiResult.Success -> {
                        val avatarSaveAvatarDto = AvatarSaveAvatarReqDto(
                            _state.value.avatarUri!!,
                            firebaseAuth.currentUser!!.uid
                        )
                        saveAvatar(avatarSaveAvatarDto, args)

                    }
                }
            }
        }
    }

    private fun saveAvatar(avatarSaveAvatarDto: AvatarSaveAvatarReqDto, args: AvatarFragmentArgs) {
        viewModelScope.launch {
            avatarSaveAvatarUseCase(avatarSaveAvatarDto).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = R.string.error_internal_server
                            )
                        }
                    }

                    ApiResult.Loading -> {
                        //nothing to do already loading
                    }

                    is ApiResult.Success -> {

                        saveDetails(result.data, args)
                    }
                }
            }
        }
    }

    private fun saveDetails(imgUrl: String, args: AvatarFragmentArgs) {
        val data = AvatarSaveDetailsReqDto(
            name = args.name,
            email = args.email,
            gender = args.gender,
            mobile = args.mobile,
            address = args.address,
            avatar = imgUrl,
            uid = firebaseAuth.currentUser!!.uid,
            dump = false,
            lat = args.lat.toDouble(),
            lng=args.lng.toDouble(),
            state = args.state,
            city = args.city,
            locality = args.locality,
            metal = false,
            medical = false,
            general = false,
            plastic = false
        )
        viewModelScope.launch {
            avatarSaveDetailsUseCase(data).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = R.string.error_internal_server
                            )
                        }
                    }

                    ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(isLoading = false, navigateToUserScreen = true)
                        }
                    }
                }

            }
        }
    }
}