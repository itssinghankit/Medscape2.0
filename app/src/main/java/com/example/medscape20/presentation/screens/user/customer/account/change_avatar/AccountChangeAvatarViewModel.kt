package com.example.medscape20.presentation.screens.user.customer.account.change_avatar

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.auth.avatar.AvatarSaveAvatarReqDto
import com.example.medscape20.domain.usecase.user.customer.account.change_avatar.AccountUpdateAvatarInDbUseCase
import com.example.medscape20.domain.usecase.user.customer.account.change_avatar.AccountUpdateAvatarUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AccountChangeAvatarStates(
    val isLoading: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val newAvatar: Uri? = null,
    val url:String?=null
)

@HiltViewModel
class AccountChangeAvatarViewModel @Inject constructor(
    private val accountUpdateAvatarUseCase: AccountUpdateAvatarUseCase,
    private val accountUpdateAvatarInDbUseCase: AccountUpdateAvatarInDbUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _states = MutableStateFlow(AccountChangeAvatarStates())
    val states: StateFlow<AccountChangeAvatarStates> = _states.asStateFlow()

    fun event(action: AccountChangeAvatarEvents) {
        when (action) {
            is AccountChangeAvatarEvents.OnNewAvatarSelected -> {
                _states.update {
                    it.copy(newAvatar = action.avatar)
                }
            }

            AccountChangeAvatarEvents.ResetErrorMessage -> {
                _states.update {
                    it.copy(errMessage = null)
                }
            }

            AccountChangeAvatarEvents.OnUpdateAvatarClicked -> {
                updateNewAvatar()
            }
        }
    }

    private fun updateNewAvatar() {
        if (states.value.newAvatar == null) {
            _states.update {
                it.copy(errMessage = R.string.error_select_image)
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val uid = firebaseAuth.currentUser!!.uid
                val updates = AvatarSaveAvatarReqDto(states.value.newAvatar!!, uid)
                accountUpdateAvatarUseCase(updates).collect { result ->
                    when (result) {
                        is ApiResult.Error -> {
                            when (result.error) {
                                DataError.Network.INTERNAL_SERVER_ERROR -> {
                                    _states.update {
                                        withContext(Dispatchers.Main) {
                                            it.copy(
                                                isLoading = false,
                                                errMessage = R.string.error_internal_server
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    _states.update {
                                        withContext(Dispatchers.Main) {
                                            it.copy(
                                                isLoading = false,
                                                errMessage = R.string.error_unknown
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        ApiResult.Loading -> {
                            _states.update {
                                withContext(Dispatchers.Main) {
                                    it.copy(isLoading = true)
                                }
                            }
                        }

                        is ApiResult.Success -> {
                            //we got the url successfully save to it database
                            updateAvatarInDataBase(url = result.data, uid)
                        }
                    }
                }
            }
        }
    }

    private fun updateAvatarInDataBase(url: String, uid: String) {
        val updates = hashMapOf<String, Any>("avatar" to url)
        viewModelScope.launch(Dispatchers.IO) {
            accountUpdateAvatarInDbUseCase(uid, updates).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            errMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            errMessage = R.string.error_unknown
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(isLoading = true)
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(isLoading = false, url = url)
                            }
                        }
                    }
                }
            }
        }
    }


}